package com.crediya.api.security.util;

public class SecurityMessages {
    
    private SecurityMessages() {
        // Utility class
    }

    // Token constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ROLE_PREFIX = "ROLE_";
    
    // Log Messages
    public static final String AUTHENTICATION_FAILED_FOR_TOKEN = "Authentication failed for token";
}