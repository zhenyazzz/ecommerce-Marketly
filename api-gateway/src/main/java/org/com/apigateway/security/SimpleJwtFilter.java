package org.com.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SimpleJwtFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(SimpleJwtFilter.class);
    private final JwtUtil jwtUtil;

    public SimpleJwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Пропускаем публичные эндпоинты
        if (isPublicEndpoint(path)) {
            log.info("Публичный эндпоинт: {}", path);
            return chain.filter(exchange);
        }

        // Получаем токен из заголовка
        String token = getJwtFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            log.warn("Токен не найден для: {}", path);
            return onError(exchange, "Токен не найден", HttpStatus.UNAUTHORIZED);
        }

        // Проверяем токен
        if (!jwtUtil.validateJwtToken(token)) {
            log.warn("Неверный токен для: {}", path);
            return onError(exchange, "Неверный токен", HttpStatus.UNAUTHORIZED);
        }

        // Получаем имя пользователя из токена
        String username = jwtUtil.getUsernameFromJwtToken(token);
        
        // Получаем userId из токена (нужно добавить в JWT)
        Long userId = jwtUtil.getUserIdFromJwtToken(token);
        
        log.info("Пользователь {} (ID: {}) обращается к: {}", username, userId, path);

        // Добавляем информацию в заголовки
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Name", username)
                .header("X-User-Id", String.valueOf(userId))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/signUp") ||
               path.startsWith("/api/auth/signIn") ||
               path.startsWith("/api/auth/validate") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info");
    }

    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
} 