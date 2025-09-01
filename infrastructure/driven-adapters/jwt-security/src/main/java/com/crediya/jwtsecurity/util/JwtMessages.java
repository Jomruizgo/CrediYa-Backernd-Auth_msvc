package com.crediya.jwtsecurity.util;

public class JwtMessages {
    
    private JwtMessages() {
        // Utility class
    }

    // Debug Messages
    public static final String TOKEN_VALIDATION_FAILED = "Token validation failed";
    public static final String FAILED_TO_EXTRACT_USERNAME = "Failed to extract username from token";
    public static final String FAILED_TO_CHECK_TOKEN_EXPIRATION = "Failed to check token expiration";
}