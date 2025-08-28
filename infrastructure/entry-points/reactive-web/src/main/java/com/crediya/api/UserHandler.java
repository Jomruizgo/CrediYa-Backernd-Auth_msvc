package com.crediya.api;

import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.api.mapper.UserRequestMapper;
import com.crediya.api.mapper.UserResponseMapper;
import com.crediya.servicePort.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final IUserService userService;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final Validator validator;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRequestDto.class)
                .doOnNext(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(userService::saveUser)
                .map(userResponseMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        return userService.findById(userId)
                .map(userResponseMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.queryParam("email").orElse("");
        return userService.findByEmail(email)
                .map(userResponseMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        return serverRequest.bodyToMono(UpdateUserRequestDto.class)
                .doOnNext(this::validateDto)
                .map(userRequestMapper::toDomain)
                .flatMap(user -> userService.updateUser(userId, user))
                .map(userResponseMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("id"));
        return userService.deleteUser(userId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return userService.findAllUsers()
                .map(userResponseMapper::toDto)
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users));
    }
    
    private <T> void validateDto(T dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
