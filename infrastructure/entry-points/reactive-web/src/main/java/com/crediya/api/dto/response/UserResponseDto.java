package com.crediya.api.dto.response;

import com.crediya.model.Role;
import com.crediya.model.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponseDto(
    Long id,
    String name,
    String lastName,
    String documentId,
    LocalDate birthDate,
    String address,
    String phoneNumber,
    String email,
    BigDecimal baseSalary,
    Role role,
    UserStatus status
) {}