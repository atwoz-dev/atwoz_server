package atwoz.atwoz.hearttransaction.command.domain.hearttransaction.exception;

public class InvalidHeartBalanceException extends RuntimeException {
    public InvalidHeartBalanceException(String message) {
        super(message);
    }
}