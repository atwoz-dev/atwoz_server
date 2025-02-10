package atwoz.atwoz.hearttransaction.command.domain.exception;

public class InvalidHeartAmountException extends RuntimeException {
    public InvalidHeartAmountException(String message) {
        super(message);
    }
}