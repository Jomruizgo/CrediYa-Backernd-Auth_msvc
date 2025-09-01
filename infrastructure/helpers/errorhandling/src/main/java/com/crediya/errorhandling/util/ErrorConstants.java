package com.crediya.errorhandling.util;

public class ErrorConstants {
    
    // Error statuses
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String CONFLICT = "CONFLICT";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    
    // Error messages
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
    
    // Exception handler log messages
    public static final String LOG_USER_DATA_VALIDATION_ERROR = "[CREDIYA-{}] User data validation error: {} at {}";
    public static final String LOG_USER_NOT_FOUND = "[CREDIYA-{}] User not found: {} at {}";
    public static final String LOG_USER_ALREADY_EXISTS = "[CREDIYA-{}] User already exists: {} at {}";
    public static final String LOG_INVALID_CREDENTIALS = "[CREDIYA-{}] Invalid credentials attempt: {}";
    public static final String LOG_VALIDATION_ERROR = "[CREDIYA-{}] Validation error: {} at {}";
    public static final String LOG_CONSTRAINT_VIOLATION = "[CREDIYA-{}] Constraint violation: {} at {}";
    public static final String LOG_UNEXPECTED_ERROR = "[CREDIYA-{}] Unexpected error occurred: {} at {}";
    public static final String VALIDATION_FAILED_PREFIX = "Validation failed: ";
    
    private ErrorConstants() {
        // Utility class
    }
}