package com.crediya.api.config.security;

import com.crediya.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::and)
                .authorizeExchange(exchanges -> 
                    exchanges
                        .pathMatchers(Constant.API_AUTH_PATH + "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        // User management - Only ADMIN and SELLER can create users
                        .pathMatchers(HttpMethod.POST, Constant.API_USER_PATH).hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.PUT, Constant.API_USER_PATH + "/**").hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.DELETE, Constant.API_USER_PATH + "/**").hasRole("ADMIN")
                        // User queries - All authenticated users can read
                        .pathMatchers(HttpMethod.GET, Constant.API_USER_PATH + "/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}