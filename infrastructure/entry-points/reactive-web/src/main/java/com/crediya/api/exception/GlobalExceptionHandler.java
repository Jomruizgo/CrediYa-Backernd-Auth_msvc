package com.crediya.api.exception;

import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.exception.InvalidCredentialsException;
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
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidUserDataException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidUserDataException(InvalidUserDataException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_USER_DATA_VALIDATION_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleUserNotFoundException(UserNotFoundException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_USER_NOT_FOUND, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
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
        logger.error(ErrorConstants.LOG_USER_ALREADY_EXISTS, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.CONFLICT,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidCredentialsException(InvalidCredentialsException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.warn(ErrorConstants.LOG_INVALID_CREDENTIALS, correlationId, ex.getMessage());
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ex.getMessage(),
            ErrorConstants.UNAUTHORIZED,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_VALIDATION_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.VALIDATION_FAILED_PREFIX + validationErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleConstraintViolationException(ConstraintViolationException ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_CONSTRAINT_VIOLATION, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        String constraintErrors = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.VALIDATION_FAILED_PREFIX + constraintErrors,
            ErrorConstants.VALIDATION_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String correlationId = getCorrelationId(exchange);
        logger.error(ErrorConstants.LOG_UNEXPECTED_ERROR, correlationId, ex.getMessage(), ex.getStackTrace()[0]);
        
        ExceptionResponse errorResponse = new ExceptionResponse(
            ErrorConstants.UNEXPECTED_ERROR,
            ErrorConstants.INTERNAL_ERROR,
            LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private String getCorrelationId(ServerWebExchange exchange) {
        // Correlation ID is guaranteed to be present thanks to CorrelationIdFilter
        return exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
    }
}