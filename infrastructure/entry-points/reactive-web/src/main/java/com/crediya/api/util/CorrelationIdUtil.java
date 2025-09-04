package com.crediya.api.util;

import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public final class CorrelationIdUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";

    private CorrelationIdUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Retrieves the correlation ID from the reactive context
     * @return Mono containing the correlation ID
     */
    public static Mono<String> getCorrelationId() {
        return Mono.deferContextual(contextView -> {
            String correlationId = contextView.getOrDefault(CORRELATION_ID_KEY, "UNKNOWN");
            return Mono.just(correlationId);
        });
    }

    /**
     * Retrieves the correlation ID from the reactive context synchronously
     * @param contextView the context view
     * @return the correlation ID or "UNKNOWN" if not found
     */
    public static String getCorrelationIdFromContext(ContextView contextView) {
        return contextView.getOrDefault(CORRELATION_ID_KEY, "UNKNOWN");
    }
}