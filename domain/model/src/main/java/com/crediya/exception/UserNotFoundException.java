package com.crediya.exception;

import com.crediya.util.Constant;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(Long id) {
        super(String.format(Constant.USER_NOT_FOUND_BY_ID, id));
    }
    
    public UserNotFoundException(String email) {
        super(String.format(Constant.USER_NOT_FOUND_BY_EMAIL, email));
    }
}