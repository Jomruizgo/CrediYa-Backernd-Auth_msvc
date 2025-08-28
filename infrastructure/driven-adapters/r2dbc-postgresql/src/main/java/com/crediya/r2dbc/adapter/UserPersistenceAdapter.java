package com.crediya.r2dbc.adapter;

import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.User;
import com.crediya.r2dbc.entity.UserEntity;
import com.crediya.r2dbc.mapper.UserEntityMapper;
import com.crediya.r2dbc.repository.UserR2dbcRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        UserEntity entity = userMapper.toEntity(user);
        return userRepository.save(entity)
                .map(userMapper::toDomain)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<User> update(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userRepository.save(entity)
                .map(userMapper::toDomain)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll()
                .map(userMapper::toDomain);
    }
}
