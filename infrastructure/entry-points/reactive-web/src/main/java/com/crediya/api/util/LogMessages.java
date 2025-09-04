package com.crediya.api.util;

public final class LogMessages {

    private LogMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // User Handler Messages
    public static final String USER_CREATION_STARTED = "[CREDIYA-{}] Starting user creation";
    public static final String USER_CREATION_SUCCESS = "[CREDIYA-{}] User created successfully with ID: {}";
    public static final String USER_CREATION_ERROR = "[CREDIYA-{}] Error creating user";
    public static final String USER_CREATION_DATA_RECEIVED = "[CREDIYA-{}] Data received for user creation: {}";
    
    public static final String USER_SEARCH_BY_ID_STARTED = "[CREDIYA-{}] Starting user search by ID: {}";
    public static final String USER_SEARCH_BY_ID_SUCCESS = "[CREDIYA-{}] User found by ID: {}";
    public static final String USER_SEARCH_BY_ID_ERROR = "[CREDIYA-{}] Error searching user by ID";
    
    public static final String USER_SEARCH_BY_EMAIL_STARTED = "[CREDIYA-{}] Starting user search by email: {}";
    public static final String USER_SEARCH_BY_EMAIL_SUCCESS = "[CREDIYA-{}] User found by email: {}";
    public static final String USER_SEARCH_BY_EMAIL_ERROR = "[CREDIYA-{}] Error searching user by email";
    
    public static final String USER_UPDATE_STARTED = "[CREDIYA-{}] Starting user update for ID: {}";
    public static final String USER_UPDATE_SUCCESS = "[CREDIYA-{}] User updated successfully with ID: {}";
    public static final String USER_UPDATE_ERROR = "[CREDIYA-{}] Error updating user";
    
    public static final String USER_DELETE_STARTED = "[CREDIYA-{}] Starting user deletion for ID: {}";
    public static final String USER_DELETE_SUCCESS = "[CREDIYA-{}] User deleted successfully with ID: {}";
    public static final String USER_DELETE_ERROR = "[CREDIYA-{}] Error deleting user";
    
    public static final String USER_LIST_ALL_STARTED = "[CREDIYA-{}] Starting to list all users";
    public static final String USER_LIST_ALL_SUCCESS = "[CREDIYA-{}] User listing completed. Total: {}";
    public static final String USER_LIST_ALL_ERROR = "[CREDIYA-{}] Error listing users";
    
    // Correlation ID Filter Messages
    public static final String CORRELATION_ID_PROCESSING = "[CREDIYA-{}] Processing request with correlation ID";
    public static final String CORRELATION_ID_GENERATED = "[CREDIYA-{}] Generated new correlation ID";
    public static final String CORRELATION_ID_FROM_HEADER = "[CREDIYA-{}] Using existing correlation ID from header";
}