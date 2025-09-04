package com.crediya.api.handler;

import com.crediya.api.dto.request.LoginRequestDto;
import com.crediya.api.dto.request.RefreshTokenRequestDto;
import com.crediya.api.mapper.AuthenticationResponseMapper;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.servicePort.IAuthenticationService;
import com.crediya.util.Constant;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final IAuthenticationService authenticationService;
    private final AuthenticationResponseMapper authenticationResponseMapper;
    private final Validator validator;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(Constant.AUTH_STARTED, correlationId);
                    
                    return serverRequest.bodyToMono(LoginRequestDto.class)
                            .flatMap(this::validateDto)
                            .flatMap(loginDto -> authenticationService.authenticate(loginDto.email(), loginDto.password()))
                            .map(authenticationResponseMapper::toDto)
                            .doOnSuccess(response -> log.info(Constant.AUTH_SUCCESS, correlationId))
                            .doOnError(error -> log.error(Constant.AUTH_FAILED, correlationId, error))
                            .flatMap(authDto -> ServerResponse.ok().bodyValue(authDto));
                });
    }

    public Mono<ServerResponse> refreshToken(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(Constant.TOKEN_REFRESH_STARTED, correlationId);
                    
                    return serverRequest.bodyToMono(RefreshTokenRequestDto.class)
                            .flatMap(this::validateDto)
                            .flatMap(refreshDto -> authenticationService.refreshToken(refreshDto.refreshToken()))
                            .map(authenticationResponseMapper::toDto)
                            .doOnSuccess(response -> log.info(Constant.TOKEN_REFRESH_SUCCESS, correlationId))
                            .doOnError(error -> log.error(Constant.TOKEN_REFRESH_FAILED, correlationId, error))
                            .flatMap(authDto -> ServerResponse.ok().bodyValue(authDto));
                });
    }


    private <T> Mono<T> validateDto(T dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(dto);
    }

}