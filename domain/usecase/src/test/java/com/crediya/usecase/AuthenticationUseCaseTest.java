package com.crediya.usecase;

import com.crediya.exception.InvalidCredentialsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IPasswordEncoderPort;
import com.crediya.gatewayPort.ITokenProviderPort;
import com.crediya.model.AuthenticationResponse;
import com.crediya.model.AuthenticationToken;
import com.crediya.model.Role;
import com.crediya.model.TokenType;
import com.crediya.model.User;
import com.crediya.model.UserStatus;
import com.crediya.servicePort.IUserService;
import com.crediya.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationUseCase Tests")
class AuthenticationUseCaseTest {

    @Mock
    private IUserService userService;
    
    @Mock
    private IPasswordEncoderPort passwordEncoder;
    
    @Mock
    private ITokenProviderPort tokenProvider;

    private AuthenticationUseCase authenticationUseCase;
    private User activeUser;
    private User pendingUser;
    private AuthenticationToken accessToken;
    private AuthenticationToken refreshToken;

    @BeforeEach
    void setUp() {
        authenticationUseCase = new AuthenticationUseCase(userService, passwordEncoder, tokenProvider);
        
        activeUser = new User(
            1L, "Juan", "Pérez", "12345678901", LocalDate.of(1990, 1, 1),
            "Calle 123", "1234567890", "juan.perez@email.com", "encodedPassword",
            new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
        );
        
        pendingUser = new User(
            2L, "Maria", "Garcia", "09876543210", LocalDate.of(1985, 5, 15),
            "Avenida 456", "0987654321", "maria.garcia@email.com", null,
            new BigDecimal("3000000"), Role.CLIENT, UserStatus.PENDING
        );
        
        accessToken = new AuthenticationToken(
            "access.jwt.token", LocalDateTime.now().plusMinutes(15), TokenType.ACCESS, 1L
        );
        
        refreshToken = new AuthenticationToken(
            "refresh.jwt.token", LocalDateTime.now().plusDays(7), TokenType.REFRESH, 1L
        );
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should authenticate successfully with valid credentials")
        void shouldAuthenticateSuccessfullyWithValidCredentials() {
            // Given
            when(userService.findByEmailInternal("juan.perez@email.com")).thenReturn(Mono.just(activeUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(tokenProvider.generateAccessToken(any(User.class))).thenReturn(accessToken);
            when(tokenProvider.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("juan.perez@email.com", "password123"))
                .expectNextMatches(response -> 
                    response.getAccessToken().equals(accessToken) &&
                    response.getRefreshToken().equals(refreshToken) &&
                    response.getUser().equals(activeUser))
                .verifyComplete();
        }
        
        @Test
        @DisplayName("Should normalize email to lowercase and trim")
        void shouldNormalizeEmailToLowercaseAndTrim() {
            // Given
            when(userService.findByEmailInternal("juan.perez@email.com")).thenReturn(Mono.just(activeUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(tokenProvider.generateAccessToken(any(User.class))).thenReturn(accessToken);
            when(tokenProvider.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("  Juan.Perez@EMAIL.COM  ", "password123"))
                .expectNextCount(1)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate(null, "password123"))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("   ", "password123"))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("juan.perez@email.com", null))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when password is empty")
        void shouldThrowExceptionWhenPasswordIsEmpty() {
            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("juan.perez@email.com", "   "))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userService.findByEmailInternal(anyString())).thenReturn(Mono.error(new UserNotFoundException("user@email.com")));

            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("notfound@email.com", "password123"))
                .expectError(InvalidCredentialsException.class)
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when user cannot login (PENDING status)")
        void shouldThrowExceptionWhenUserCannotLogin() {
            // Given
            when(userService.findByEmailInternal("maria.garcia@email.com")).thenReturn(Mono.just(pendingUser));

            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("maria.garcia@email.com", "password123"))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when password does not match")
        void shouldThrowExceptionWhenPasswordDoesNotMatch() {
            // Given
            when(userService.findByEmailInternal("juan.perez@email.com")).thenReturn(Mono.just(activeUser));
            when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

            // When & Then
            StepVerifier.create(authenticationUseCase.authenticate("juan.perez@email.com", "wrongpassword"))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_CREDENTIALS))
                .verify();
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            // Given
            String validRefreshToken = "valid.refresh.token";
            when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(tokenProvider.extractUsername(validRefreshToken)).thenReturn("juan.perez@email.com");
            when(userService.findByEmailInternal("juan.perez@email.com")).thenReturn(Mono.just(activeUser));
            when(tokenProvider.generateAccessToken(any(User.class))).thenReturn(accessToken);
            when(tokenProvider.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

            // When & Then
            StepVerifier.create(authenticationUseCase.refreshToken(validRefreshToken))
                .expectNextMatches(response -> 
                    response.getAccessToken().equals(accessToken) &&
                    response.getRefreshToken().equals(refreshToken) &&
                    response.getUser().equals(activeUser))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when refresh token is null")
        void shouldThrowExceptionWhenRefreshTokenIsNull() {
            // When & Then
            StepVerifier.create(authenticationUseCase.refreshToken(null))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_REFRESH_TOKEN))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when refresh token is empty")
        void shouldThrowExceptionWhenRefreshTokenIsEmpty() {
            // When & Then
            StepVerifier.create(authenticationUseCase.refreshToken("   "))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_REFRESH_TOKEN))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when refresh token is invalid")
        void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
            // Given
            String invalidRefreshToken = "invalid.refresh.token";
            when(tokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

            // When & Then
            StepVerifier.create(authenticationUseCase.refreshToken(invalidRefreshToken))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_REFRESH_TOKEN))
                .verify();
        }

        @Test
        @DisplayName("Should throw exception when user not found during refresh")
        void shouldThrowExceptionWhenUserNotFoundDuringRefresh() {
            // Given
            String validRefreshToken = "valid.refresh.token";
            when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(tokenProvider.extractUsername(validRefreshToken)).thenReturn("notfound@email.com");
            when(userService.findByEmailInternal("notfound@email.com")).thenReturn(Mono.error(new UserNotFoundException("notfound@email.com")));

            // When & Then
            StepVerifier.create(authenticationUseCase.refreshToken(validRefreshToken))
                .expectErrorMatches(ex -> ex instanceof InvalidCredentialsException &&
                    ex.getMessage().equals(Constant.INVALID_REFRESH_TOKEN))
                .verify();
        }
    }
}