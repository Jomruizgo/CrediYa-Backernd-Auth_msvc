package com.crediya.api.exception;

import com.crediya.exception.InvalidUserDataException;
import com.crediya.exception.UserAlreadyExistsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.errorhandling.ExceptionResponse;
import com.crediya.errorhandling.util.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidUserDataException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidUserDataException(InvalidUserDataException ex) {
        logger.error("User data validation error: {} at {}", ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {} at {}", ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.NOT_FOUND,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleUserAlreadyExistsException(UserAlreadyExistsException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] User already exists: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.CONFLICT,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleValidationException(WebExchangeBindException ex) {
        logger.error("Validation error: {} at {}", ex.getMessage(), ex.getStackTrace()[0]);
        
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            "Validation failed: " + validationErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("Constraint violation: {} at {}", ex.getMessage(), ex.getStackTrace()[0]);
        
        String constraintErrors = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            "Validation failed: " + constraintErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error("[CREDIYA-{}] Unexpected error occurred: {} at {}", correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.UNEXPECTED_ERROR,
            ErrorConstants.INTERNAL_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private String getCorrelationId(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        return correlationId;
    }
}