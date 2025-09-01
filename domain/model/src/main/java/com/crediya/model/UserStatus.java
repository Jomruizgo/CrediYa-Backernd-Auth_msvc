package com.crediya.model;

public enum UserStatus {
    PENDING,           // Registered by admin, no password yet
    AWAITING_SETUP,    // Activated, waiting for user to set password
    ACTIVE,            // Has password, can login
    INACTIVE           // Deactivated
}