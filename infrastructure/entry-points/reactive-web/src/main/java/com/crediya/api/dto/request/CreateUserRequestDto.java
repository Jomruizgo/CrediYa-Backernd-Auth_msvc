package com.crediya.api.dto.request;

import com.crediya.model.Role;
import com.crediya.model.UserStatus;
import com.crediya.util.Constant;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequestDto(
    @NotBlank(message = Constant.DTO_NAME_REQUIRED)
    String name,
    
    @NotBlank(message = Constant.DTO_LASTNAME_REQUIRED)
    String lastName,
    
    String documentId,
    
    LocalDate birthDate,
    
    String address,
    
    String phoneNumber,
    
    @NotBlank(message = Constant.DTO_EMAIL_REQUIRED)
    @Email(message = Constant.DTO_EMAIL_INVALID)
    String email,
    
    // Password is optional - for admin registration without password
    @Size(min = 8, message = Constant.DTO_PASSWORD_MIN_LENGTH)
    String password,
    
    @NotNull(message = Constant.DTO_SALARY_REQUIRED)
    @DecimalMin(value = "0", message = Constant.DTO_SALARY_MIN)
    @DecimalMax(value = "15000000", message = Constant.DTO_SALARY_MAX)
    BigDecimal baseSalary,
    
    // Role is optional - defaults to CLIENT in UseCase
    Role role,
    
    // Status is optional - defaults to PENDING in UseCase
    UserStatus status
) {}