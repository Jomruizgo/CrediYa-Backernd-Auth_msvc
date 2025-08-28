package com.crediya.usecase;

import com.crediya.exception.InvalidUserDataException;
import com.crediya.exception.UserAlreadyExistsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IUserPersistencePort;
import com.crediya.model.User;
import com.crediya.servicePort.IUserService;
import com.crediya.util.Constant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class UserUseCase implements IUserService {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constant.EMAIL_REGEX);
    
    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public Mono<User> saveUser(User user) {
        return validateRequiredFields(user)
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validateSalaryRange)
                .flatMap(validUser -> validateEmailNotExists(validUser.getEmail())
                        .then(Mono.just(validUser)))
                .flatMap(userPersistencePort::save);
    }

    @Override
    public Mono<User> findById(Long id) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        return userPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_EMAIL));
        }
        return userPersistencePort.findByEmail(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new UserNotFoundException(email)));
    }

    @Override
    public Mono<User> updateUser(Long id, User user) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        
        return userPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .then(validateRequiredFields(user))
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validateSalaryRange)
                .flatMap(validUser -> validateEmailNotExistsForUpdate(validUser.getEmail(), id)
                        .then(Mono.just(validUser)))
                .map(validUser -> new User(
                        id, 
                        validUser.getName(), 
                        validUser.getLastName(), 
                        validUser.getBirthDate(),
                        validUser.getAddress(), 
                        validUser.getPhoneNumber(), 
                        validUser.getEmail(), 
                        validUser.getBaseSalary(),
                        validUser.getRole()
                ))
                .flatMap(userPersistencePort::update);
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        if (id == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_ID));
        }
        
        return userPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .then(userPersistencePort.deleteById(id));
    }

    @Override
    public Flux<User> findAllUsers() {
        return userPersistencePort.findAll();
    }

    private Mono<User> validateRequiredFields(User user) {
        if (user == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_USER_DATA));
        }
        
        if (isNullOrEmpty(user.getName()) || 
            isNullOrEmpty(user.getLastName()) || 
            isNullOrEmpty(user.getEmail()) || 
            user.getBaseSalary() == null) {
            return Mono.error(new InvalidUserDataException(Constant.INVALID_REQUIRED_FIELDS));
        }
        
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
        return userPersistencePort.findByEmail(email)
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(email)))
                .then();
    }

    private Mono<Void> validateEmailNotExistsForUpdate(String email, Long currentUserId) {
        return userPersistencePort.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(email)))
                .then();
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}