package com.epam.finaltask.exception.custom;

import org.springframework.validation.FieldError;

import java.util.List;

public class UpdateUserException extends RuntimeException {
    private final List<FieldError> fieldErrors;

    public UpdateUserException(String message) {
        super(message);
        this.fieldErrors = null;
    }

    public UpdateUserException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}