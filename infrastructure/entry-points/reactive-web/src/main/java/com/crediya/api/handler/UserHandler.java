package com.crediya.api.handler;

import com.crediya.api.docs.UserApiDocs;
import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.api.mapper.UserRequestMapper;
import com.crediya.api.mapper.UserResponseMapper;
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
@Tag(name = "Users", description = "Operations related to users")
public class UserHandler extends UserApiDocs {

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
        String correlationId = getCorrelationId(serverRequest);
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_SEARCH_BY_ID_STARTED, correlationId, userId);
        return userService.findById(userId)
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_ID_SUCCESS, correlationId, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_ID_ERROR, correlationId, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        String email = serverRequest.queryParam("email").orElse("");
        log.info(LogMessages.USER_SEARCH_BY_EMAIL_STARTED, correlationId, email);
        return userService.findByEmail(email)
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_SEARCH_BY_EMAIL_SUCCESS, correlationId, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_SEARCH_BY_EMAIL_ERROR, correlationId, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_UPDATE_STARTED, correlationId, userId);
        return serverRequest.bodyToMono(UpdateUserRequestDto.class)
                .doOnNext(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(user -> userService.updateUser(userId, user))
                .map(userResponseMapper::toDto)
                .doOnSuccess(user -> log.info(LogMessages.USER_UPDATE_SUCCESS, correlationId, user.id()))
                .doOnError(error -> log.error(LogMessages.USER_UPDATE_ERROR, correlationId, error))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.USER_DELETE_STARTED, correlationId, userId);
        return userService.deleteUser(userId)
                .doOnSuccess(unused -> log.info(LogMessages.USER_DELETE_SUCCESS, correlationId, userId))
                .doOnError(error -> log.error(LogMessages.USER_DELETE_ERROR, correlationId, error))
                .then(ServerResponse.noContent().build())
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        log.info(LogMessages.USER_LIST_ALL_STARTED, correlationId);
        return userService.findAllUsers()
                .map(userResponseMapper::toDto)
                .collectList()
                .doOnSuccess(users -> log.info(LogMessages.USER_LIST_ALL_SUCCESS, correlationId, users.size()))
                .doOnError(error -> log.error(LogMessages.USER_LIST_ALL_ERROR, correlationId, error))
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
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
