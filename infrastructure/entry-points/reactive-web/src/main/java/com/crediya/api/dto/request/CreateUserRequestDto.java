package com.crediya.api.dto.request;

import com.crediya.model.Role;
import com.crediya.util.Constant;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequestDto(
    @NotBlank(message = Constant.DTO_NAME_REQUIRED)
    String name,
    
    @NotBlank(message = Constant.DTO_LASTNAME_REQUIRED)
    String lastName,
    
    LocalDate birthDate,
    
    String address,
    
    String phoneNumber,
    
    @NotBlank(message = Constant.DTO_EMAIL_REQUIRED)
    @Email(message = Constant.DTO_EMAIL_INVALID)
    String email,
    
    @NotNull(message = Constant.DTO_SALARY_REQUIRED)
    @DecimalMin(value = "0", message = Constant.DTO_SALARY_MIN)
    @DecimalMax(value = "15000000", message = Constant.DTO_SALARY_MAX)
    BigDecimal baseSalary,
    
    @NotNull(message = Constant.DTO_ROLE_REQUIRED)
    Role role
) {}