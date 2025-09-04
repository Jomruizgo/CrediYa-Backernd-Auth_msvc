package com.crediya.api.handler;

import com.crediya.api.docs.UserApiDocs;
import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.api.mapper.UserRequestMapper;
import com.crediya.api.mapper.UserResponseMapper;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.api.util.LogMessages;
import com.crediya.servicePort.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to users")
public class UserHandler extends UserApiDocs {

    private final IUserService userService;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final Validator validator;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(LogMessages.USER_CREATION_STARTED, correlationId);
        return serverRequest.bodyToMono(CreateUserRequestDto.class)
                .doOnNext(dto -> log.debug(LogMessages.USER_CREATION_DATA_RECEIVED, correlationId, dto.email()))
                .flatMap(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(user -> ReactiveSecurityContextHolder.getContext()
                        .map(ctx -> ctx.getAuthentication().getAuthorities().iterator().next().getAuthority())
                        .map(authority -> authority.replace("ROLE_", ""))
                        .flatMap(creatorRole -> userService.saveUser(user, creatorRole)))
                .map(userResponseMapper::toDto)
                            .doOnSuccess(user -> log.info(LogMessages.USER_CREATION_SUCCESS, correlationId, user.id()))
                            .doOnError(error -> log.error(LogMessages.USER_CREATION_ERROR, correlationId, error))
                            .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
                });
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    Long userId = Long.valueOf(serverRequest.pathVariable("id"));
                    log.info(LogMessages.USER_SEARCH_BY_ID_STARTED, correlationId, userId);
                    return Mono.just(userId)
                            .flatMap(userService::findById)
                            .map(userResponseMapper::toDto)
                            .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_ID_SUCCESS, correlationId, user.id()))
                            .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_ID_ERROR, correlationId, error))
                            .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
                });
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    String email = serverRequest.queryParam("email").orElse("");
                    log.info(LogMessages.USER_SEARCH_BY_EMAIL_STARTED, correlationId, email);
                    return Mono.just(email)
                            .flatMap(userService::findByEmail)
                            .map(userResponseMapper::toDto)
                            .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_EMAIL_SUCCESS, correlationId, user.id()))
                            .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_EMAIL_ERROR, correlationId, error))
                            .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
                });
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    Long userId = Long.valueOf(serverRequest.pathVariable("id"));
                    log.info(LogMessages.USER_UPDATE_STARTED, correlationId, userId);
                    return serverRequest.bodyToMono(UpdateUserRequestDto.class)
                            .flatMap(this::validateDto)
                            .map(userRequestMapper::toDomain)
                            .flatMap(user -> userService.updateUser(userId, user))
                            .map(userResponseMapper::toDto)
                            .doOnSuccess(user -> log.info(LogMessages.USER_UPDATE_SUCCESS, correlationId, user.id()))
                            .doOnError(error -> log.error(LogMessages.USER_UPDATE_ERROR, correlationId, error))
                            .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
                });
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    Long userId = Long.valueOf(serverRequest.pathVariable("id"));
                    log.info(LogMessages.USER_DELETE_STARTED, correlationId, userId);
                    return Mono.just(userId)
                            .flatMap(userService::deleteUser)
                            .doOnSuccess(unused -> log.info(LogMessages.USER_DELETE_SUCCESS, correlationId, userId))
                            .doOnError(error -> log.error(LogMessages.USER_DELETE_ERROR, correlationId, error))
                            .then(ServerResponse.noContent().build());
                });
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(LogMessages.USER_LIST_ALL_STARTED, correlationId);
                    return Mono.empty()
                            .thenMany(userService.findAllUsers())
                            .map(userResponseMapper::toDto)
                            .collectList()
                            .doOnSuccess(users -> log.info(LogMessages.USER_LIST_ALL_SUCCESS, correlationId, users.size()))
                            .doOnError(error -> log.error(LogMessages.USER_LIST_ALL_ERROR, correlationId, error))
                            .flatMap(users -> ServerResponse.ok().bodyValue(users));
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
