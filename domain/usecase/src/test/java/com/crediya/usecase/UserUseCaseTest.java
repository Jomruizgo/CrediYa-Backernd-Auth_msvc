package com.crediya.usecase;

import com.crediya.exception.InvalidUserDataException;
import com.crediya.exception.UserAlreadyExistsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IPasswordEncoderPort;
import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.Role;
import com.crediya.model.User;
import com.crediya.model.UserStatus;
import com.crediya.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase Tests")
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;
    
    @Mock
    private IPasswordEncoderPort passwordEncoder;

    private UserUseCase userUseCase;
    private User validUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userPersistencePort, passwordEncoder);
        
        validUser = new User(
            null,
            "Juan",
            "Pérez",
            "12345678901",
            LocalDate.of(1990, 1, 1),
            "Calle 123",
            "1234567890",
            "juan.perez@email.com",
            "password123",
            new BigDecimal("2000000"),
            Role.CLIENT,
            UserStatus.ACTIVE
        );
        
        existingUser = new User(
            1L,
            "Juan",
            "Pérez",
            "12345678901",
            LocalDate.of(1990, 1, 1),
            "Calle 123",
            "1234567890",
            "juan.perez@email.com",
            "encodedPassword",
            new BigDecimal("2000000"),
            Role.CLIENT,
            UserStatus.ACTIVE
        );
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully when ADMIN creates CLIENT")
        void shouldSaveUserSuccessfullyWhenAdminCreatesClient() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId(anyString())).thenReturn(Mono.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser, "ADMIN"))
                .expectNext(existingUser)
                .verifyComplete();
        }
        
        @Test
        @DisplayName("Should save user successfully when SELLER creates CLIENT")
        void shouldSaveUserSuccessfullyWhenSellerCreatesClient() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId(anyString())).thenReturn(Mono.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser, "SELLER"))
                .expectNext(existingUser)
                .verifyComplete();
        }
        
        @Test
        @DisplayName("Should throw exception when CLIENT tries to create user")
        void shouldThrowExceptionWhenClientTriesToCreateUser() {
            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser, "CLIENT"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_USER_CREATION))
                .verify();
        }
        
        @Test
        @DisplayName("Should throw exception when SELLER tries to create ADMIN")
        void shouldThrowExceptionWhenSellerTriesToCreateAdmin() {
            // Given
            User adminUser = new User(
                null, "Admin", "User", "99999999999", LocalDate.now(),
                "Address", "123456789", "admin@email.com", "password",
                new BigDecimal("5000000"), Role.ADMIN, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(adminUser, "SELLER"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_ADMIN_CREATION))
                .verify();
        }
        
        @Test
        @DisplayName("Should throw exception when SELLER tries to create another SELLER")
        void shouldThrowExceptionWhenSellerTriesToCreateSeller() {
            // Given
            User sellerUser = new User(
                null, "Seller", "User", "88888888888", LocalDate.now(),
                "Address", "123456789", "seller@email.com", "password",
                new BigDecimal("3000000"), Role.SELLER, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(sellerUser, "SELLER"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_ADMIN_CREATION))
                .verify();
        }
        
        @Test
        @DisplayName("Should allow ADMIN to create ADMIN")
        void shouldAllowAdminToCreateAdmin() {
            // Given
            User adminUser = new User(
                null, "Admin", "User", "77777777777", LocalDate.now(),
                "Address", "123456789", "admin2@email.com", "password",
                new BigDecimal("5000000"), Role.ADMIN, UserStatus.ACTIVE
            );
            User savedAdmin = new User(
                2L, "Admin", "User", "77777777777", LocalDate.now(),
                "Address", "123456789", "admin2@email.com", "encodedPassword",
                new BigDecimal("5000000"), Role.ADMIN, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId(anyString())).thenReturn(Mono.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(savedAdmin));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(adminUser, "ADMIN"))
                .expectNext(savedAdmin)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when user is null")
        void shouldThrowExceptionWhenUserIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.saveUser(null, "ADMIN"))
                .expectError(InvalidUserDataException.class)
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            User userWithNullName = new User(
                null, null, "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullName, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            User userWithEmptyName = new User(
                null, "   ", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithEmptyName, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when lastName is null")
        void shouldThrowExceptionWhenLastNameIsNull() {
            // Given
            User userWithNullLastName = new User(
                null, "Juan", null, "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullLastName, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given
            User userWithNullEmail = new User(
                null, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", null, "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullEmail, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when baseSalary is null")
        void shouldThrowExceptionWhenBaseSalaryIsNull() {
            // Given
            User userWithNullSalary = new User(
                null, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                null, Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullSalary, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            // Given
            User userWithInvalidEmail = new User(
                null, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "invalid-email", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithInvalidEmail, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_EMAIL_FORMAT))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when salary is below minimum")
        void shouldThrowExceptionWhenSalaryIsBelowMinimum() {
            // Given
            User userWithLowSalary = new User(
                null, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("-1"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithLowSalary, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_SALARY_RANGE))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when salary is above maximum")
        void shouldThrowExceptionWhenSalaryIsAboveMaximum() {
            // Given
            User userWithHighSalary = new User(
                null, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("20000000"), Role.CLIENT, UserStatus.ACTIVE
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithHighSalary, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_SALARY_RANGE))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser, "ADMIN"))
                .expectError(UserAlreadyExistsException.class)
                .verify();
        }

        @Test
        @DisplayName("Should save user successfully when documentId is null")
        void shouldSaveUserSuccessfullyWhenDocumentIdIsNull() {
            // Given
            User userWithNullDocumentId = new User(
                null, "Juan", "Pérez", null, LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(userWithNullDocumentId));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullDocumentId, "ADMIN"))
                .expectNext(userWithNullDocumentId)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should save user successfully when documentId is empty")
        void shouldSaveUserSuccessfullyWhenDocumentIdIsEmpty() {
            // Given
            User userWithEmptyDocumentId = new User(
                null, "Juan", "Pérez", "   ", LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(userWithEmptyDocumentId));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithEmptyDocumentId, "ADMIN"))
                .expectNext(userWithEmptyDocumentId)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when documentId already exists")
        void shouldThrowExceptionWhenDocumentIdAlreadyExists() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof UserAlreadyExistsException &&
                    ex.getMessage().contains("Document ID 12345678901 is already registered"))
                .verify();
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find user by id successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.findById(1L))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when id is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.findById(null))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_ID))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userPersistencePort.findById(1L)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.findById(1L))
                .expectError(UserNotFoundException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Find By Email Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email successfully")
        void shouldFindUserByEmailSuccessfully() {
            // Given
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.findByEmail("juan.perez@email.com"))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find user by email with trim and lowercase")
        void shouldFindUserByEmailWithTrimAndLowercase() {
            // Given
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.findByEmail("  Juan.Perez@EMAIL.COM  "))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.findByEmail(null))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_EMAIL))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // When & Then
            StepVerifier.create(userUseCase.findByEmail("   "))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_EMAIL))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFoundByEmail() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.findByEmail("notfound@email.com"))
                .expectError(UserNotFoundException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Find By Document ID Tests")
    class FindByDocumentIdTests {

        @Test
        @DisplayName("Should find user by documentId successfully")
        void shouldFindUserByDocumentIdSuccessfully() {
            // Given
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.findByDocumentId("12345678901"))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when documentId is null")
        void shouldThrowExceptionWhenDocumentIdIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.findByDocumentId(null))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals("Document ID is required for search"))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when documentId is empty")
        void shouldThrowExceptionWhenDocumentIdIsEmpty() {
            // When & Then
            StepVerifier.create(userUseCase.findByDocumentId("   "))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals("Document ID is required for search"))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found by documentId")
        void shouldThrowExceptionWhenUserNotFoundByDocumentId() {
            // Given
            when(userPersistencePort.findByDocumentId(anyString())).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.findByDocumentId("99999999999"))
                .expectError(UserNotFoundException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User updatedUser = new User(
                1L, "Juan Updated", "Pérez Updated", "99999999999", LocalDate.now(),
                "New Address", "987654321", "juan.updated@email.com", "newPassword",
                new BigDecimal("3000000"), Role.ADMIN, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.updated@email.com")).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId("99999999999")).thenReturn(Mono.empty());
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(updatedUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, new User(
                null, "Juan Updated", "Pérez Updated", "99999999999", LocalDate.now(),
                "New Address", "987654321", "juan.updated@email.com", "newPassword",
                new BigDecimal("3000000"), Role.ADMIN, UserStatus.ACTIVE
            ), "ADMIN"))
                .expectNext(updatedUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should allow updating user with same email")
        void shouldAllowUpdatingUserWithSameEmail() {
            // Given
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when id is null")
        void shouldThrowExceptionWhenIdIsNullForUpdate() {
            // When & Then
            StepVerifier.create(userUseCase.updateUser(null, validUser, "ADMIN"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_ID))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to update not found")
        void shouldThrowExceptionWhenUserToUpdateNotFound() {
            // Given
            when(userPersistencePort.findById(anyLong())).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.updateUser(999L, validUser, "ADMIN"))
                .expectError(UserNotFoundException.class)
                .verify();
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email exists for another user")
        void shouldThrowExceptionWhenEmailExistsForAnotherUser() {
            // Given
            User anotherUser = new User(
                2L, "Another", "User", "99999999999", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(anotherUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectError(UserAlreadyExistsException.class)
                .verify();
        }

        @Test
        @DisplayName("Should update user successfully when documentId is null")
        void shouldUpdateUserSuccessfullyWhenDocumentIdIsNull() {
            // Given
            User userWithNullDocumentId = new User(
                null, "Juan", "Pérez", null, LocalDate.now(),
                "Address", "123456789", "test@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("test@email.com")).thenReturn(Mono.empty());
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(userWithNullDocumentId));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, userWithNullDocumentId, "ADMIN"))
                .expectNext(userWithNullDocumentId)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when documentId exists for another user")
        void shouldThrowExceptionWhenDocumentIdExistsForAnotherUser() {
            // Given
            User anotherUser = new User(
                2L, "Another", "User", "12345678901", LocalDate.now(),
                "Address", "123456789", "another@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.empty());
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(anotherUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectError(UserAlreadyExistsException.class)
                .verify();
        }

        @Test
        @DisplayName("Should allow ADMIN to update CLIENT user")
        void shouldAllowAdminToUpdateClientUser() {
            // Given
            User clientUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(clientUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectNext(clientUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should allow SELLER to update CLIENT user")
        void shouldAllowSellerToUpdateClientUser() {
            // Given
            User clientUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(clientUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(clientUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "SELLER"))
                .expectNext(clientUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should deny CLIENT to update any user")
        void shouldDenyClientToUpdateUser() {
            // Given
            User clientUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(clientUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "CLIENT"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_OPERATION))
                .verify();
        }

        @Test
        @DisplayName("Should deny SELLER to update SELLER user")
        void shouldDenySellerToUpdateSellerUser() {
            // Given
            User sellerUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.SELLER, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(sellerUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "SELLER"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_OPERATION))
                .verify();
        }

        @Test
        @DisplayName("Should deny SELLER to update ADMIN user")
        void shouldDenySellerToUpdateAdminUser() {
            // Given
            User adminUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.ADMIN, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(adminUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "SELLER"))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.UNAUTHORIZED_OPERATION))
                .verify();
        }

        @Test
        @DisplayName("Should allow ADMIN to update SELLER user")
        void shouldAllowAdminToUpdateSellerUser() {
            // Given
            User sellerUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.SELLER, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(sellerUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(sellerUser));
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(sellerUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(sellerUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectNext(sellerUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should allow ADMIN to update ADMIN user")
        void shouldAllowAdminToUpdateAdminUser() {
            // Given
            User adminUser = new User(
                1L, "Juan", "Pérez", "12345678901", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com", "password",
                new BigDecimal("2000000"), Role.ADMIN, UserStatus.ACTIVE
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(adminUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(adminUser));
            when(userPersistencePort.findByDocumentId("12345678901")).thenReturn(Mono.just(adminUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(adminUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser, "ADMIN"))
                .expectNext(adminUser)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.deleteById(1L)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.deleteUser(1L))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when id is null")
        void shouldThrowExceptionWhenIdIsNullForDelete() {
            // When & Then
            StepVerifier.create(userUseCase.deleteUser(null))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_ID))
                .verify();
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to delete not found")
        void shouldThrowExceptionWhenUserToDeleteNotFound() {
            // Given
            when(userPersistencePort.findById(anyLong())).thenReturn(Mono.empty());
            when(userPersistencePort.deleteById(anyLong())).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.deleteUser(999L))
                .expectError(UserNotFoundException.class)
                .verify();
        }
    }

    @Nested
    @DisplayName("Find All Users Tests")
    class FindAllUsersTests {

        @Test
        @DisplayName("Should return all users successfully")
        void shouldReturnAllUsersSuccessfully() {
            // Given
            User user1 = new User(1L, "Juan", "Pérez", "11111111111", LocalDate.now(), "Address", "123", "juan@email.com", "password", new BigDecimal("2000000"), Role.CLIENT, UserStatus.ACTIVE);
            User user2 = new User(2L, "Ana", "García", "22222222222", LocalDate.now(), "Address", "456", "ana@email.com", "password", new BigDecimal("3000000"), Role.ADMIN, UserStatus.ACTIVE);
            
            when(userPersistencePort.findAll()).thenReturn(Flux.just(user1, user2));

            // When & Then
            StepVerifier.create(userUseCase.findAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should return empty flux when no users exist")
        void shouldReturnEmptyFluxWhenNoUsersExist() {
            // Given
            when(userPersistencePort.findAll()).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(userUseCase.findAllUsers())
                .verifyComplete();
        }
    }
}