package com.epam.finaltask.exception.custom;

public class CurrentUserNotFoundException extends RuntimeException{
    public CurrentUserNotFoundException(String message) {
        super(message);
    }

    public CurrentUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
