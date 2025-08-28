package com.crediya.exception;

import com.crediya.util.Constant;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String email) {
        super(String.format(Constant.USER_ALREADY_EXISTS, email));
    }
    
    public static UserAlreadyExistsException forDocumentId(String documentId) {
        return new UserAlreadyExistsException(String.format(Constant.USER_DOCUMENT_ALREADY_EXISTS, documentId));
    }
}