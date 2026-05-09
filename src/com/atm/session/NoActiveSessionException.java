package com.atm.session;

public class NoActiveSessionException extends RuntimeException {
    public NoActiveSessionException(String message) {
        super(message);
    }
    public NoActiveSessionException(String message, Throwable cause) {
        super(message, cause);
    }

}
