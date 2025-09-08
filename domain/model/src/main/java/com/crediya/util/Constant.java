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
    public static final String INVALID_REQUIRED_FIELDS = "Name, lastName, email and baseSalary are required fields";
    public static final String INVALID_SALARY_RANGE = "Base salary must be between 0 and 15,000,000";
    public static final String INVALID_USER_DATA = "Invalid user data";
    public static final String UNAUTHORIZED_OPERATION = "User does not have permission to perform this operation";
    public static final String INVALID_ID = "ID cannot be null";
    public static final String INVALID_EMAIL = "Email cannot be null or empty";
    public static final String INVALID_EMAIL_FORMAT = "Email must have a valid format";
    public static final String UNAUTHORIZED_USER_CREATION = "Only ADMIN and SELLER roles can create users";
    public static final String UNAUTHORIZED_ADMIN_CREATION = "Only ADMIN can create SELLER or ADMIN users";



    // Authentication Messages
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    
    // Authorization Messages
    public static final String ACCESS_DENIED_NOT_OWNER = "Access denied: You can only access your own user information";
    public static final String ACCESS_DENIED_EMAIL_SEARCH = "Access denied: Clients cannot search users by email";
    public static final String ACCESS_DENIED_LIST_ALL = "Access denied: Clients cannot list all users";
    public static final String ACCESS_DENIED_INVALID_ROLE = "Access denied: Invalid role";

    // Authentication Log Messages
    public static final String AUTH_STARTED = "Authentication started for correlationId: {}";
    public static final String AUTH_SUCCESS = "Authentication successful for correlationId: {}";
    public static final String AUTH_FAILED = "Authentication failed for correlationId: {}";
    public static final String TOKEN_REFRESH_STARTED = "Token refresh started for correlationId: {}";
    public static final String TOKEN_REFRESH_SUCCESS = "Token refresh successful for correlationId: {}";
    public static final String TOKEN_REFRESH_FAILED = "Token refresh failed for correlationId: {}";

    // API Paths
    public static final String API_USER_PATH = "/api/v1/user";
    public static final String API_AUTH_PATH = "/api/v1/auth";
    public static final String API_AUTH_LOGIN_PATH = API_AUTH_PATH + "/login";
    public static final String API_AUTH_REFRESH_PATH = API_AUTH_PATH + "/refresh";
    
    // DTO Validation Messages (Entry Point Layer)
    public static final String DTO_NAME_REQUIRED = "Name field is mandatory";
    public static final String DTO_LASTNAME_REQUIRED = "Last name field is mandatory";
    public static final String DTO_EMAIL_REQUIRED = "Email field is mandatory";
    public static final String DTO_EMAIL_INVALID = "Invalid email format provided";
    public static final String DTO_PASSWORD_REQUIRED = "Password field is mandatory";
    public static final String DTO_PASSWORD_MIN_LENGTH = "Password must be at least 8 characters long";
    public static final String DTO_REFRESH_TOKEN_REQUIRED = "Refresh token is required";
    public static final String DTO_SALARY_REQUIRED = "Base salary field is mandatory";
    public static final String DTO_SALARY_MIN = "Base salary cannot be less than 0";
    public static final String DTO_SALARY_MAX = "Base salary cannot exceed 15,000,000";
    public static final String DTO_ROLE_REQUIRED = "Role field is mandatory";

    // OpenAPI Documentation Messages
    public static final String OPENAPI_LOGIN_REQUEST_DESC = "Login request with user credentials";
    public static final String OPENAPI_EMAIL_DESC = "User email address";
    public static final String OPENAPI_EMAIL_EXAMPLE = "user@crediya.com";
    public static final String OPENAPI_PASSWORD_DESC = "User password (minimum 8 characters)";
    public static final String OPENAPI_PASSWORD_EXAMPLE = "securePassword123";
    public static final String OPENAPI_REFRESH_TOKEN_REQUEST_DESC = "Request to refresh access token";
    public static final String OPENAPI_REFRESH_TOKEN_DESC = "Refresh token for obtaining new access token";
    public static final String OPENAPI_AUTH_RESPONSE_DESC = "Authentication response with tokens and user information";
    public static final String OPENAPI_ACCESS_TOKEN_DESC = "JWT access token for API authentication";
    public static final String OPENAPI_REFRESH_TOKEN_RESPONSE_DESC = "Refresh token for getting new access tokens";
    public static final String OPENAPI_USER_INFO_DESC = "Authenticated user information";
}