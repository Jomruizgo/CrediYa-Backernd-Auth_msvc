package com.crediya.util;

import java.math.BigDecimal;

public class Constant {
    
    private Constant() {
        // Utility class
    }

    // Validation
    public static final BigDecimal MINIMUM_SALARY = BigDecimal.ZERO;
    public static final BigDecimal MAXIMUM_SALARY = BigDecimal.valueOf(15000000);
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Error Messages
    public static final String USER_ALREADY_EXISTS = "User with email %s already exists";
    public static final String USER_DOCUMENT_ALREADY_EXISTS = "Document ID %s is already registered by another user";
    public static final String USER_NOT_FOUND_BY_ID = "User with ID %s not found";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User with email %s not found";
    public static final String USER_NOT_FOUND_BY_DOCUMENT_ID = "User with document ID %s not found";
    public static final String INVALID_REQUIRED_FIELDS = "Name, lastName, email and baseSalary are required fields";
    public static final String INVALID_SALARY_RANGE = "Base salary must be between 0 and 15,000,000";
    public static final String INVALID_EMAIL_DUPLICATE = "Email is already registered by another user";
    public static final String INVALID_DOCUMENT_ID_DUPLICATE = "Document ID is already registered by another user";
    public static final String INVALID_USER_DATA = "Invalid user data";
    public static final String INVALID_ID = "ID cannot be null";
    public static final String INVALID_EMAIL = "Email cannot be null or empty";
    public static final String INVALID_EMAIL_FORMAT = "Email must have a valid format";
    public static final String INVALID_DOCUMENT_ID = "Document ID cannot be null or empty";

    // Success Messages
    public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    // API Paths
    public static final String API_USER_PATH = "/api/v1/user";
    
    // DTO Validation Messages (Entry Point Layer)
    public static final String DTO_NAME_REQUIRED = "Name field is mandatory";
    public static final String DTO_LASTNAME_REQUIRED = "Last name field is mandatory";
    public static final String DTO_EMAIL_REQUIRED = "Email field is mandatory";
    public static final String DTO_EMAIL_INVALID = "Invalid email format provided";
    public static final String DTO_SALARY_REQUIRED = "Base salary field is mandatory";
    public static final String DTO_SALARY_MIN = "Base salary cannot be less than 0";
    public static final String DTO_SALARY_MAX = "Base salary cannot exceed 15,000,000";
    public static final String DTO_ROLE_REQUIRED = "Role field is mandatory";
}