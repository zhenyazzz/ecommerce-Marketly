package org.com.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public reactor.core.publisher.Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                                                    org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        log.info("➡️ Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("⬅️ Response status: {}",
                            exchange.getResponse().getStatusCode());
                }));
    }

    @Override
    public int getOrder() {
        return -1; // фильтр с высоким приоритетом
    }
}
