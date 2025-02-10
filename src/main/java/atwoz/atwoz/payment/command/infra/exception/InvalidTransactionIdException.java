package atwoz.atwoz.payment.command.infra.exception;

public class InvalidTransactionIdException extends RuntimeException {
    public InvalidTransactionIdException(Exception e) {
        super("유효하지 않은 Transaction ID 입니다.", e);
    }
}
