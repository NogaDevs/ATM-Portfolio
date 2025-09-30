package com.atm.exception;

public class InvalidInputError extends RuntimeException {
    public InvalidInputError(String message) {
        super(message);
    }
    public InvalidInputError(String message, Throwable cause) {
        super(message, cause);
    }
}
