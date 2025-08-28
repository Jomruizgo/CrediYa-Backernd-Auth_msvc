package com.crediya.r2dbc.repository;

import com.crediya.model.Role;
import com.crediya.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserR2dbcRepositoryTest {

    @Mock
    private UserR2dbcRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
            .id(1L)
            .name("John")
            .lastName("Doe")
            .birthDate(LocalDate.of(1990, 5, 15))
            .address("123 Main St")
            .phoneNumber("1234567890")
            .email("john.doe@email.com")
            .baseSalary(new BigDecimal("2500000.00"))
            .role(Role.CLIENT)
            .build();
    }

    @Test
    void shouldSaveUser() {
        when(userRepository.save(testUser)).thenReturn(Mono.just(testUser));
        
        StepVerifier.create(userRepository.save(testUser))
            .expectNext(testUser)
            .verifyComplete();
            
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail("john.doe@email.com"))
            .thenReturn(Mono.just(testUser));
        
        StepVerifier.create(userRepository.findByEmail("john.doe@email.com"))
            .expectNext(testUser)
            .verifyComplete();
            
        verify(userRepository).findByEmail("john.doe@email.com");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("nonexistent@email.com"))
            .thenReturn(Mono.empty());
        
        StepVerifier.create(userRepository.findByEmail("nonexistent@email.com"))
            .verifyComplete();
            
        verify(userRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUser));
        
        StepVerifier.create(userRepository.findById(1L))
            .expectNext(testUser)
            .verifyComplete();
            
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(userRepository.findById(999L)).thenReturn(Mono.empty());
        
        StepVerifier.create(userRepository.findById(999L))
            .verifyComplete();
            
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.deleteById(1L)).thenReturn(Mono.empty());
        
        StepVerifier.create(userRepository.deleteById(1L))
            .verifyComplete();
            
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldFindAllUsers() {
        UserEntity anotherUser = UserEntity.builder()
            .id(2L)
            .name("Jane")
            .lastName("Smith")
            .email("jane.smith@email.com")
            .baseSalary(new BigDecimal("3000000.00"))
            .role(Role.ADMIN)
            .build();

        when(userRepository.findAll()).thenReturn(Flux.just(testUser, anotherUser));
        
        StepVerifier.create(userRepository.findAll())
            .expectNext(testUser)
            .expectNext(anotherUser)
            .verifyComplete();
            
        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Flux.empty());
        
        StepVerifier.create(userRepository.findAll())
            .verifyComplete();
            
        verify(userRepository).findAll();
    }

    @Test
    void shouldHandleDifferentRoles() {
        UserEntity adminUser = testUser.toBuilder()
            .id(2L)
            .email("admin@email.com")
            .role(Role.ADMIN)
            .build();

        when(userRepository.findByEmail("admin@email.com"))
            .thenReturn(Mono.just(adminUser));
        
        StepVerifier.create(userRepository.findByEmail("admin@email.com"))
            .expectNext(adminUser)
            .verifyComplete();
            
        verify(userRepository).findByEmail("admin@email.com");
    }
}