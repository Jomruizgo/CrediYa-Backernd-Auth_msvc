package com.crediya.servicePort;

import com.crediya.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> saveUser(User user, String creatorRole);
    Mono<User> findById(Long targetUserId, Long authenticatedUserId, String authenticatedUserRole);
    Mono<User> findByEmail(String email, Long authenticatedUserId, String authenticatedUserRole);
    Mono<User> findByDocumentId(String documentId);
    Mono<User> updateUser(Long id, User user, String updaterRole);
    Mono<Void> deleteUser(Long id);
    Flux<User> findAllUsers(Long authenticatedUserId, String authenticatedUserRole);
    
    // Internal methods for authentication (bypass authorization)
    Mono<User> findByEmailInternal(String email);
    Mono<User> findByIdInternal(Long id);
}