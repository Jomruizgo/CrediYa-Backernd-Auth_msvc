package com.crediya.exception;

public class InvalidUserDataException extends RuntimeException {
    
    public InvalidUserDataException(String message) {
        super(message);
    }
}