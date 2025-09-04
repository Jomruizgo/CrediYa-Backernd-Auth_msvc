package com.crediya.usecase;

import com.crediya.exception.InvalidUserDataException;
import com.crediya.exception.UserAlreadyExistsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IPasswordEncoderPort;
import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.Role;
import com.crediya.model.User;
import com.crediya.model.UserStatus;
import com.crediya.servicePort.IUserService;
import com.crediya.util.Constant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class UserUseCase implements IUserService {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constant.EMAIL_REGEX);
    
    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoder;

    public UserUseCase(IUserPersistencePort userPersistencePort, IPasswordEncoderPort passwordEncoder) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<User> saveUser(User user, String creatorRole) {
        return validateUserCreationPermission(user, creatorRole)
                .flatMap(this::validateRequiredFields)
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validateSalaryRange)
                .flatMap(validUser -> validateEmailNotExists(validUser.getEmail())
                        .then(validateDocumentIdNotExists(validUser.getDocumentId()))
                        .then(Mono.just(validUser)))
                .map(this::encryptPassword)
                .flatMap(userPersistencePort::save);
    }

    @Override
    public Mono<User> findById(Long id) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        return Mono.just(id)
                .flatMap(userPersistencePort::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return Mono.fromSupplier(() -> {
            if (email == null || email.trim().isEmpty()) {
                throw new InvalidUserDataException(Constant.INVALID_EMAIL);
            }
            return email.trim().toLowerCase();
        })
        .flatMap(userPersistencePort::findByEmail)
        .switchIfEmpty(Mono.error(new UserNotFoundException(email)));
    }

    @Override
    public Mono<User> findByDocumentId(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Document ID is required for search"));
        }
        return Mono.just(documentId.trim())
                .flatMap(userPersistencePort::findByDocumentId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with document ID " + documentId + " not found")));
    }

    @Override
    public Mono<User> updateUser(Long id, User user) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        
        return Mono.just(id)
                .flatMap(userPersistencePort::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .then(validateRequiredFields(user))
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validateSalaryRange)
                .flatMap(validUser -> validateEmailNotExistsForUpdate(validUser.getEmail(), id)
                        .then(validateDocumentIdNotExistsForUpdate(validUser.getDocumentId(), id))
                        .then(Mono.just(validUser)))
                .map(validUser -> new User(
                        id, 
                        validUser.getName(), 
                        validUser.getLastName(), 
                        validUser.getDocumentId(),
                        validUser.getBirthDate(),
                        validUser.getAddress(), 
                        validUser.getPhoneNumber(), 
                        validUser.getEmail(),
                        validUser.getPassword(),
                        validUser.getBaseSalary(),
                        validUser.getRole(),
                        validUser.getStatus()
                ))
                .flatMap(userPersistencePort::update);
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        
        return Mono.just(id)
                .flatMap(userPersistencePort::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .then(userPersistencePort.deleteById(id));
    }

    @Override
    public Flux<User> findAllUsers() {
        return Mono.empty()
                .thenMany(userPersistencePort.findAll());
    }

    private Mono<User> validateRequiredFields(User user) {
        if (user == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_USER_DATA));
        }
        
        if (isNullOrEmpty(user.getName()) || 
            isNullOrEmpty(user.getLastName()) || 
            isNullOrEmpty(user.getEmail()) ||
            user.getBaseSalary() == null ||
            user.getStatus() == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_REQUIRED_FIELDS));
        }
        
        // For ACTIVE users, password is required
        if (user.getStatus() == UserStatus.ACTIVE && !user.hasCredentials()) {
            return Mono.error(new InvalidUserDataException("Active users require password"));
        }
        
        // For PENDING/AWAITING_SETUP users, password is optional
        return Mono.just(user);
    }

    private Mono<User> validateEmailFormat(User user) {
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_EMAIL_FORMAT));
        }
        return Mono.just(user);
    }

    private Mono<User> validateSalaryRange(User user) {
        BigDecimal salary = user.getBaseSalary();
        if (salary.compareTo(Constant.MINIMUM_SALARY) < 0 || 
            salary.compareTo(Constant.MAXIMUM_SALARY) > 0) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_SALARY_RANGE));
        }
        return Mono.just(user);
    }

    private Mono<Void> validateEmailNotExists(String email) {
        return Mono.just(email)
                .flatMap(userPersistencePort::findByEmail)
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(email)))
                .then();
    }

    private Mono<Void> validateEmailNotExistsForUpdate(String email, Long currentUserId) {
        return Mono.just(email)
                .flatMap(userPersistencePort::findByEmail)
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(email)))
                .then();
    }

    private Mono<Void> validateDocumentIdNotExists(String documentId) {
        // Si documentId es null o vacío, no validamos (es opcional)
        if (isNullOrEmpty(documentId)) {
            return Mono.empty();
        }
        
        return Mono.just(documentId.trim())
                .flatMap(userPersistencePort::findByDocumentId)
                .flatMap(existingUser -> Mono.error(UserAlreadyExistsException.forDocumentId(documentId)))
                .then();
    }

    private Mono<Void> validateDocumentIdNotExistsForUpdate(String documentId, Long currentUserId) {
        // Si documentId es null o vacío, no validamos (es opcional)
        if (isNullOrEmpty(documentId)) {
            return Mono.empty();
        }
        
        return Mono.just(documentId.trim())
                .flatMap(userPersistencePort::findByDocumentId)
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .flatMap(existingUser -> Mono.error(UserAlreadyExistsException.forDocumentId(documentId)))
                .then();
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Mono<User> validateUserCreationPermission(User userToCreate, String creatorRole) {
        // First check if user is null
        if (userToCreate == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_USER_DATA));
        }
        
        // Only ADMIN and SELLER can create users
        if (!Role.ADMIN.name().equals(creatorRole) && !Role.SELLER.name().equals(creatorRole)) {
            return Mono.error(new InvalidUserDataException(Constant.UNAUTHORIZED_USER_CREATION));
        }
        
        // Only ADMIN can create SELLER or ADMIN users
        if ((userToCreate.getRole() == Role.ADMIN || userToCreate.getRole() == Role.SELLER) 
            && !Role.ADMIN.name().equals(creatorRole)) {
            return Mono.error(new InvalidUserDataException(Constant.UNAUTHORIZED_ADMIN_CREATION));
        }
        
        return Mono.just(userToCreate);
    }
    
    private User encryptPassword(User user) {
        // Only encrypt if password exists
        if (!user.hasCredentials()) {
            return user;
        }
        
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        return new User(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getDocumentId(),
                user.getBirthDate(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                encryptedPassword,
                user.getBaseSalary(),
                user.getRole(),
                user.getStatus()
        );
    }
}