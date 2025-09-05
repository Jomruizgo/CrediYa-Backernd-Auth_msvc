package com.crediya.api.config;

import com.crediya.api.util.LogMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter implements WebFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = extractOrGenerateCorrelationId(exchange);
        
        log.debug(LogMessages.CORRELATION_ID_PROCESSING, correlationId);
        
        // Add correlation ID to request header if it was generated or modify existing one
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();
        
        // Add correlation ID to response header for downstream services (before response is committed)
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);
        
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        
        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(CORRELATION_ID_KEY, correlationId));
    }

    private String extractOrGenerateCorrelationId(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
            log.debug(LogMessages.CORRELATION_ID_GENERATED, correlationId);
        } else {
            log.debug(LogMessages.CORRELATION_ID_FROM_HEADER, correlationId);
        }
        
        return correlationId;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}