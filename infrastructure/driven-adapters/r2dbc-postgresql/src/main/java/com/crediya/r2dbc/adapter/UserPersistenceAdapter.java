package com.crediya.r2dbc.adapter;

import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.User;
import com.crediya.r2dbc.entity.UserEntity;
import com.crediya.r2dbc.mapper.UserEntityMapper;
import com.crediya.r2dbc.repository.UserR2dbcRepository;
import com.crediya.r2dbc.util.LogMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserPersistenceAdapter implements IUserPersistencePort {

    private final UserR2dbcRepository userRepository;
    private final UserEntityMapper userMapper;
    private final TransactionalOperator transactionalOperator;

    public UserPersistenceAdapter(UserR2dbcRepository userRepository, UserEntityMapper userMapper, TransactionalOperator transactionalOperator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> save(User user) {
        log.info(LogMessages.USER_SAVE_STARTED);
        UserEntity entity = userMapper.toEntity(user);
        return userRepository.save(entity)
                .map(userMapper::toDomain)
                .doOnSuccess(savedUser -> log.info(LogMessages.USER_SAVE_SUCCESS, savedUser.getId()))
                .doOnError(error -> log.error(LogMessages.USER_SAVE_ERROR, error))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findById(Long id) {
        log.info(LogMessages.USER_FIND_BY_ID_STARTED, id);
        return userRepository.findById(id)
                .map(userMapper::toDomain)
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.info(LogMessages.USER_FIND_BY_ID_SUCCESS, id);
                    } else {
                        log.info(LogMessages.USER_FIND_BY_ID_NOT_FOUND, id);
                    }
                })
                .doOnError(error -> log.error(LogMessages.USER_FIND_BY_ID_ERROR, error));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "NO_CONTEXT");
            log.info(LogMessages.USER_FIND_BY_EMAIL_STARTED, correlationId, email);
            return userRepository.findByEmail(email)
                    .map(userMapper::toDomain)
                    .doOnSuccess(user -> {
                        if (user != null) {
                            log.info(LogMessages.USER_FIND_BY_EMAIL_SUCCESS, correlationId, email);
                        } else {
                            log.info(LogMessages.USER_FIND_BY_EMAIL_NOT_FOUND, correlationId, email);
                        }
                    })
                    .doOnError(error -> log.error(LogMessages.USER_FIND_BY_EMAIL_ERROR, correlationId, error));
        });
    }

    @Override
    public Mono<User> update(User user) {
        log.info(LogMessages.USER_UPDATE_STARTED, user.getId());
        UserEntity entity = userMapper.toEntity(user);
        return userRepository.save(entity)
                .map(userMapper::toDomain)
                .doOnSuccess(updatedUser -> log.info(LogMessages.USER_UPDATE_SUCCESS, updatedUser.getId()))
                .doOnError(error -> log.error(LogMessages.USER_UPDATE_ERROR, error))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.info(LogMessages.USER_DELETE_STARTED, id);
        return userRepository.deleteById(id)
                .doOnSuccess(unused -> log.info(LogMessages.USER_DELETE_SUCCESS, id))
                .doOnError(error -> log.error(LogMessages.USER_DELETE_ERROR, error))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Flux<User> findAll() {
        log.info(LogMessages.USER_FIND_ALL_STARTED);
        return userRepository.findAll()
                .map(userMapper::toDomain)
                .doOnComplete(() -> log.info(LogMessages.USER_FIND_ALL_SUCCESS, "completed"))
                .doOnError(error -> log.error(LogMessages.USER_FIND_ALL_ERROR, error));
    }
}
