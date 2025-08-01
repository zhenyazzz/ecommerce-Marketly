package org.com.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Генерируем уникальный ID для запроса
        String requestId = UUID.randomUUID().toString();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String remoteAddress = request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        LocalDateTime startTime = LocalDateTime.now();

        // Добавляем request ID в заголовки
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .header("X-Start-Time", startTime.toString())
                .build();

        // Логируем входящий запрос
        log.info("=== ВХОДЯЩИЙ ЗАПРОС ===");
        log.info("Request ID: {}", requestId);
        log.info("Method: {}", method);
        log.info("Path: {}", path);
        log.info("Remote IP: {}", remoteAddress);
        log.info("Headers: {}", request.getHeaders());
        log.info("Query Params: {}", request.getQueryParams());
        log.info("Start Time: {}", startTime);

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    LocalDateTime endTime = LocalDateTime.now();
                    long duration = java.time.Duration.between(startTime, endTime).toMillis();
                    
                    // Логируем завершение запроса
                    log.info("=== ЗАВЕРШЕНИЕ ЗАПРОСА ===");
                    log.info("Request ID: {}", requestId);
                    log.info("Method: {}", method);
                    log.info("Path: {}", path);
                    log.info("Duration: {}ms", duration);
                    log.info("Status: {}", exchange.getResponse().getStatusCode());
                    log.info("End Time: {}", endTime);
                    log.info("========================");
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Высокий приоритет - логируем первым
    }
} 