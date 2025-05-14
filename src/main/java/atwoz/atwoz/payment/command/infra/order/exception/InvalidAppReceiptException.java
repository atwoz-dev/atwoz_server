package atwoz.atwoz.payment.command.infra.order.exception;

public class InvalidAppReceiptException extends RuntimeException {
    public InvalidAppReceiptException(Exception e) {
        super("유효하지 않은 App Receipt 입니다.", e);
    }

    public InvalidAppReceiptException(String message) {
        super(message);
    }
}
