package com.crediya.servicePort;

import com.crediya.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> saveUser(User user);
    Mono<User> findById(Long id);
    Mono<User> findByEmail(String email);
    Mono<User> findByDocumentId(String documentId);
    Mono<User> updateUser(Long id, User user);
    Mono<Void> deleteUser(Long id);
    Flux<User> findAllUsers();
}