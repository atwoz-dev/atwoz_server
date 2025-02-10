package atwoz.atwoz.hearttransaction.command.domain.exception;

public class InvalidHeartBalanceException extends RuntimeException {
    public InvalidHeartBalanceException(String message) {
        super(message);
    }
}