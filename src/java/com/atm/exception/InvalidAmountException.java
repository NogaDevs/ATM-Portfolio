package java.com.atm.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidAmountException(String message) {
        super(message);
    }
}
