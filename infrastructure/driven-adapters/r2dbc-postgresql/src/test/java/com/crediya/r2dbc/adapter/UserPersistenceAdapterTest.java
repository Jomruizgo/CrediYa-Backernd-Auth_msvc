package com.crediya.r2dbc.adapter;

import com.crediya.model.Role;
import com.crediya.model.User;
import com.crediya.model.UserStatus;
import com.crediya.r2dbc.entity.UserEntity;
import com.crediya.r2dbc.mapper.UserEntityMapper;
import com.crediya.r2dbc.repository.UserR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserR2dbcRepository userRepository;
    
    @Mock
    private UserEntityMapper userMapper;
    
    @Mock
    private TransactionalOperator transactionalOperator;

    private UserPersistenceAdapter userPersistenceAdapter;
    
    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        userPersistenceAdapter = new UserPersistenceAdapter(userRepository, userMapper, transactionalOperator);
        
        testUser = new User(1L, "John", "Doe", "12345678901", LocalDate.of(1990, 5, 15),
                           "123 Main St", "1234567890", "john.doe@email.com",
                           "password123", new BigDecimal("2500000"), Role.CLIENT, UserStatus.ACTIVE);
        
        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setName("John");
        testUserEntity.setLastName("Doe");
        testUserEntity.setEmail("john.doe@email.com");
        testUserEntity.setBaseSalary(new BigDecimal("2500000"));
        testUserEntity.setRole(Role.CLIENT);
    }

    @Test
    void shouldSaveUser() {
        when(userMapper.toEntity(testUser)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(Mono.just(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userPersistenceAdapter.save(testUser))
                .expectNext(testUser)
                .verifyComplete();

        verify(userMapper).toEntity(testUser);
        verify(userRepository).save(testUserEntity);
        verify(userMapper).toDomain(testUserEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);

        StepVerifier.create(userPersistenceAdapter.findById(1L))
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository).findById(1L);
        verify(userMapper).toDomain(testUserEntity);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userPersistenceAdapter.findById(999L))
                .verifyComplete();

        verify(userRepository).findById(999L);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail("john.doe@email.com")).thenReturn(Mono.just(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);

        StepVerifier.create(userPersistenceAdapter.findByEmail("john.doe@email.com"))
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository).findByEmail("john.doe@email.com");
        verify(userMapper).toDomain(testUserEntity);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userPersistenceAdapter.findByEmail("notfound@email.com"))
                .verifyComplete();

        verify(userRepository).findByEmail("notfound@email.com");
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void shouldUpdateUser() {
        when(userMapper.toEntity(testUser)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(Mono.just(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userPersistenceAdapter.update(testUser))
                .expectNext(testUser)
                .verifyComplete();

        verify(userMapper).toEntity(testUser);
        verify(userRepository).save(testUserEntity);
        verify(userMapper).toDomain(testUserEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldDeleteUserById() {
        when(userRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userPersistenceAdapter.deleteById(1L))
                .verifyComplete();

        verify(userRepository).deleteById(1L);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFindAllUsers() {
        User anotherUser = new User(2L, "Jane", "Smith", "98765432109", LocalDate.of(1985, 8, 20),
                                   "456 Oak St", "0987654321", "jane.smith@email.com",
                                   "password456", new BigDecimal("3000000"), Role.ADMIN, UserStatus.ACTIVE);
        
        UserEntity anotherUserEntity = new UserEntity();
        anotherUserEntity.setId(2L);
        anotherUserEntity.setName("Jane");
        anotherUserEntity.setLastName("Smith");
        anotherUserEntity.setEmail("jane.smith@email.com");

        when(userRepository.findAll()).thenReturn(Flux.just(testUserEntity, anotherUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);
        when(userMapper.toDomain(anotherUserEntity)).thenReturn(anotherUser);

        StepVerifier.create(userPersistenceAdapter.findAll())
                .expectNext(testUser)
                .expectNext(anotherUser)
                .verifyComplete();

        verify(userRepository).findAll();
        verify(userMapper).toDomain(testUserEntity);
        verify(userMapper).toDomain(anotherUserEntity);
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userPersistenceAdapter.findAll())
                .verifyComplete();

        verify(userRepository).findAll();
        verify(userMapper, never()).toDomain(any());
    }
}