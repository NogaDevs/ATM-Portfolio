package com.atm.exception;

public class LockedCustomerException extends RuntimeException {
    public LockedCustomerException(String message) {
        super(message);
    }
    public LockedCustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
