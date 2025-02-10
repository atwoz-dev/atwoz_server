package atwoz.atwoz.hearttransaction.domain.exception;

public class InvalidHeartAmountException extends RuntimeException {
    public InvalidHeartAmountException(String message) {
        super(message);
    }
}