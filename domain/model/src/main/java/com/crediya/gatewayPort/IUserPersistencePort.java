package com.crediya.gatewayPort;

import com.crediya.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserPersistencePort {
    Mono<User> save(User user);
    Mono<User> findById(Long id);
    Mono<User> findByEmail(String email);
    Mono<User> findByDocumentId(String documentId);
    Mono<User> update(User user);
    Mono<Void> deleteById(Long id);
    Flux<User> findAll();
}