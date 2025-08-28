package com.crediya.r2dbc.util;

public final class LogMessages {

    private LogMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // User Persistence Adapter Messages  
    public static final String USER_SAVE_STARTED = "[CREDIYA-DB] Starting to save user in database";
    public static final String USER_SAVE_SUCCESS = "[CREDIYA-DB] User saved successfully in database with ID: {}";
    public static final String USER_SAVE_ERROR = "[CREDIYA-DB] Error saving user in database";
    
    public static final String USER_FIND_BY_ID_STARTED = "[CREDIYA-DB] Starting to find user by ID: {}";
    public static final String USER_FIND_BY_ID_SUCCESS = "[CREDIYA-DB] User found in database by ID: {}";
    public static final String USER_FIND_BY_ID_NOT_FOUND = "[CREDIYA-DB] User not found in database by ID: {}";
    public static final String USER_FIND_BY_ID_ERROR = "[CREDIYA-DB] Error finding user by ID in database";
    
    public static final String USER_FIND_BY_EMAIL_STARTED = "[CREDIYA-DB-{}] Starting to find user by email: {}";
    public static final String USER_FIND_BY_EMAIL_SUCCESS = "[CREDIYA-DB-{}] User found in database by email: {}";
    public static final String USER_FIND_BY_EMAIL_NOT_FOUND = "[CREDIYA-DB-{}] User not found in database by email: {}";
    public static final String USER_FIND_BY_EMAIL_ERROR = "[CREDIYA-DB-{}] Error finding user by email in database";
    
    public static final String USER_UPDATE_STARTED = "[CREDIYA-DB] Starting to update user in database with ID: {}";
    public static final String USER_UPDATE_SUCCESS = "[CREDIYA-DB] User updated successfully in database with ID: {}";
    public static final String USER_UPDATE_ERROR = "[CREDIYA-DB] Error updating user in database";
    
    public static final String USER_DELETE_STARTED = "[CREDIYA-DB] Starting to delete user in database with ID: {}";
    public static final String USER_DELETE_SUCCESS = "[CREDIYA-DB] User deleted successfully from database with ID: {}";
    public static final String USER_DELETE_ERROR = "[CREDIYA-DB] Error deleting user from database";
    
    public static final String USER_FIND_ALL_STARTED = "[CREDIYA-DB] Starting to find all users in database";
    public static final String USER_FIND_ALL_SUCCESS = "[CREDIYA-DB] All users retrieved from database. Total: {}";
    public static final String USER_FIND_ALL_ERROR = "[CREDIYA-DB] Error finding all users in database";
    
    public static final String USER_EXISTS_BY_EMAIL_STARTED = "[CREDIYA-DB] Starting to check if user exists by email: {}";
    public static final String USER_EXISTS_BY_EMAIL_SUCCESS = "[CREDIYA-DB] User existence check completed for email: {}. Exists: {}";
    public static final String USER_EXISTS_BY_EMAIL_ERROR = "[CREDIYA-DB] Error checking user existence by email";
}