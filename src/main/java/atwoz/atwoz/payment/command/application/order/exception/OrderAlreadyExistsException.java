package atwoz.atwoz.payment.command.application.order.exception;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException() {
        super("이미 존재하는 주문입니다.");
    }
}
