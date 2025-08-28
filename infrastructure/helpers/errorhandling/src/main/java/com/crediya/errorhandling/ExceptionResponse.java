package com.crediya.errorhandling;

import java.time.LocalDateTime;

public record ExceptionResponse(
    String message,
    String status,
    LocalDateTime timestamp
) {
}