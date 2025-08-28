package com.crediya.api;

import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.api.mapper.UserRequestMapper;
import com.crediya.api.mapper.UserResponseMapper;
import com.crediya.api.util.LogMessages;
import com.crediya.servicePort.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final IUserService userService;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final Validator validator;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        log.info(LogMessages.USER_CREATION_STARTED, correlationId);
        return serverRequest.bodyToMono(CreateUserRequestDto.class)
                .doOnNext(dto -> log.debug(LogMessages.USER_CREATION_DATA_RECEIVED, correlationId, dto.email()))
                .doOnNext(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(userService::saveUser)
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_CREATION_SUCCESS, correlationId, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_CREATION_ERROR, correlationId, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_SEARCH_BY_ID_STARTED, userId);
        return userService.findById(userId)
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_ID_SUCCESS, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_ID_ERROR, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.queryParam("email").orElse("");
        log.info(LogMessages.USER_SEARCH_BY_EMAIL_STARTED, email);
        return userService.findByEmail(email)
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_EMAIL_SUCCESS, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_EMAIL_ERROR, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_UPDATE_STARTED, userId);
        return serverRequest.bodyToMono(UpdateUserRequestDto.class)
                .doOnNext(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(user -> userService.updateUser(userId, user))
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_UPDATE_SUCCESS, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_UPDATE_ERROR, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_DELETE_STARTED, userId);
        return userService.deleteUser(userId)
                .doOnSuccess(unused -> log.info(LogMessages.USER_DELETE_SUCCESS, userId))
                .doOnError(error -> log.error(LogMessages.USER_DELETE_ERROR, error))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        log.info(LogMessages.USER_LIST_ALL_STARTED);
        return userService.findAllUsers()
                .map(userResponseMapper::toDto)
                .collectList()
                .doOnSuccess(users -> log.info(LogMessages.USER_LIST_ALL_SUCCESS, users.size()))
                .doOnError(error -> log.error(LogMessages.USER_LIST_ALL_ERROR, error))
                .flatMap(users -> ServerResponse.ok().bodyValue(users));
    }
    
    private <T> void validateDto(T dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
    
    private String getCorrelationId(ServerRequest request) {
        String correlationId = request.headers().firstHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        return correlationId;
    }
}
