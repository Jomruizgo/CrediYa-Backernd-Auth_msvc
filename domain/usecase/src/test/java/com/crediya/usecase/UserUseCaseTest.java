package com.crediya.usecase;

import com.crediya.exception.InvalidUserDataException;
import com.crediya.exception.UserAlreadyExistsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.Role;
import com.crediya.model.User;
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

    private UserUseCase userUseCase;
    private User validUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userPersistencePort);
        
        validUser = new User(
            null,
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 1),
            "Calle 123",
            "1234567890",
            "juan.perez@email.com",
            new BigDecimal("2000000"),
            Role.CLIENT
        );
        
        existingUser = new User(
            1L,
            "Juan",
            "Pérez", 
            LocalDate.of(1990, 1, 1),
            "Calle 123",
            "1234567890",
            "juan.perez@email.com",
            new BigDecimal("2000000"),
            Role.CLIENT
        );
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully when all validations pass")
        void shouldSaveUserSuccessfully() {
            // Given
            when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());
            when(userPersistencePort.save(any(User.class))).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.saveUser(validUser))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when user is null")
        void shouldThrowExceptionWhenUserIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.saveUser(null))
                .expectError(InvalidUserDataException.class)
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            User userWithNullName = new User(
                null, null, "Pérez", LocalDate.now(),
                "Address", "123456789", "test@email.com",
                new BigDecimal("2000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullName))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            User userWithEmptyName = new User(
                null, "   ", "Pérez", LocalDate.now(),
                "Address", "123456789", "test@email.com",
                new BigDecimal("2000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithEmptyName))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when lastName is null")
        void shouldThrowExceptionWhenLastNameIsNull() {
            // Given
            User userWithNullLastName = new User(
                null, "Juan", null, LocalDate.now(),
                "Address", "123456789", "test@email.com",
                new BigDecimal("2000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullLastName))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given
            User userWithNullEmail = new User(
                null, "Juan", "Pérez", LocalDate.now(),
                "Address", "123456789", null,
                new BigDecimal("2000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullEmail))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when baseSalary is null")
        void shouldThrowExceptionWhenBaseSalaryIsNull() {
            // Given
            User userWithNullSalary = new User(
                null, "Juan", "Pérez", LocalDate.now(),
                "Address", "123456789", "test@email.com",
                null, Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithNullSalary))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_REQUIRED_FIELDS))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            // Given
            User userWithInvalidEmail = new User(
                null, "Juan", "Pérez", LocalDate.now(),
                "Address", "123456789", "invalid-email",
                new BigDecimal("2000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithInvalidEmail))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_EMAIL_FORMAT))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when salary is below minimum")
        void shouldThrowExceptionWhenSalaryIsBelowMinimum() {
            // Given
            User userWithLowSalary = new User(
                null, "Juan", "Pérez", LocalDate.now(),
                "Address", "123456789", "test@email.com",
                new BigDecimal("-1"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithLowSalary))
                .expectErrorMatches(ex -> ex instanceof InvalidUserDataException &&
                    ex.getMessage().equals(Constant.INVALID_SALARY_RANGE))
                .verify();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when salary is above maximum")
        void shouldThrowExceptionWhenSalaryIsAboveMaximum() {
            // Given
            User userWithHighSalary = new User(
                null, "Juan", "Pérez", LocalDate.now(),
                "Address", "123456789", "test@email.com",
                new BigDecimal("20000000"), Role.CLIENT
            );

            // When & Then
            StepVerifier.create(userUseCase.saveUser(userWithHighSalary))
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
            StepVerifier.create(userUseCase.saveUser(validUser))
                .expectError(UserAlreadyExistsException.class)
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
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User updatedUser = new User(
                1L, "Juan Updated", "Pérez Updated", LocalDate.now(),
                "New Address", "987654321", "juan.updated@email.com",
                new BigDecimal("3000000"), Role.ADMIN
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.updated@email.com")).thenReturn(Mono.empty());
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(updatedUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, new User(
                null, "Juan Updated", "Pérez Updated", LocalDate.now(),
                "New Address", "987654321", "juan.updated@email.com",
                new BigDecimal("3000000"), Role.ADMIN
            )))
                .expectNext(updatedUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should allow updating user with same email")
        void shouldAllowUpdatingUserWithSameEmail() {
            // Given
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.update(any(User.class))).thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser))
                .expectNext(existingUser)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when id is null")
        void shouldThrowExceptionWhenIdIsNullForUpdate() {
            // When & Then
            StepVerifier.create(userUseCase.updateUser(null, validUser))
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
            StepVerifier.create(userUseCase.updateUser(999L, validUser))
                .expectError(UserNotFoundException.class)
                .verify();
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email exists for another user")
        void shouldThrowExceptionWhenEmailExistsForAnotherUser() {
            // Given
            User anotherUser = new User(
                2L, "Another", "User", LocalDate.now(),
                "Address", "123456789", "juan.perez@email.com",
                new BigDecimal("2000000"), Role.CLIENT
            );
            
            when(userPersistencePort.findById(1L)).thenReturn(Mono.just(existingUser));
            when(userPersistencePort.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(anotherUser));

            // When & Then
            StepVerifier.create(userUseCase.updateUser(1L, validUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();
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
            User user1 = new User(1L, "Juan", "Pérez", LocalDate.now(), "Address", "123", "juan@email.com", new BigDecimal("2000000"), Role.CLIENT);
            User user2 = new User(2L, "Ana", "García", LocalDate.now(), "Address", "456", "ana@email.com", new BigDecimal("3000000"), Role.ADMIN);
            
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