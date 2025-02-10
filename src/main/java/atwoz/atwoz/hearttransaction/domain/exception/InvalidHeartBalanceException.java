package atwoz.atwoz.hearttransaction.domain.exception;

public class InvalidHeartBalanceException extends RuntimeException {
    public InvalidHeartBalanceException(String message) {
        super(message);
    }
}