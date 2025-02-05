package atwoz.atwoz.payment.application.exception;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException() {
        super("주문이 유효하지 않습니다.");
    }
}
