package com.epam.finaltask.exception.custom;

public class NotEnoughMoneyToPurchaseException extends RuntimeException{
    public NotEnoughMoneyToPurchaseException(String message) {
        super(message);
    }

    public NotEnoughMoneyToPurchaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
