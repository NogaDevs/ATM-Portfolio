package com.atm.exception;

public class DuplicateCardNumberException extends RuntimeException {
    public DuplicateCardNumberException(String message, Throwable cause) { super(message, cause);}
    public DuplicateCardNumberException(String message) {
        super(message);
    }
}
