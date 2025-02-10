package atwoz.atwoz.hearttransaction.command.domain.exception;

public class InvalidHeartTransactionTypeException extends RuntimeException {
    public InvalidHeartTransactionTypeException(String message) {
        super(message);
    }
}
