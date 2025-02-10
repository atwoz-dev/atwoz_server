package atwoz.atwoz.hearttransaction.command.domain.hearttransaction.exception;

public class InvalidHeartAmountException extends RuntimeException {
    public InvalidHeartAmountException(String message) {
        super(message);
    }
}