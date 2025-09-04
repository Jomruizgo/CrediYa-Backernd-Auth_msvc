package com.crediya.api.config.security;

import com.crediya.api.util.SecurityMessages;
import com.crediya.gatewayPort.ITokenProviderPort;
import com.crediya.servicePort.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {
    
    private final ITokenProviderPort tokenProvider;
    private final IUserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        
        if (token != null && tokenProvider.validateToken(token)) {
            return authenticateToken(token, exchange)
                    .flatMap(authentication -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                    .onErrorResume(error -> {
                        log.debug(SecurityMessages.AUTHENTICATION_FAILED_FOR_TOKEN, error);
                        return chain.filter(exchange);
                    });
        }
        
        return chain.filter(exchange);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(SecurityMessages.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityMessages.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Mono<UsernamePasswordAuthenticationToken> authenticateToken(String token, ServerWebExchange exchange) {
        String username = tokenProvider.extractUsername(token);
        if (username != null) {
            String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
            return userService.findByEmail(username)
                    .map(user -> {
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority(SecurityMessages.ROLE_PREFIX + user.getRole().name())
                        );
                        return new UsernamePasswordAuthenticationToken(username, null, authorities);
                    })
                    .cast(UsernamePasswordAuthenticationToken.class)
                    .contextWrite(ctx -> ctx.put("correlationId", correlationId));
        }
        return Mono.empty();
    }
    
}