package atwoz.atwoz.payment.command.application.heartpurchaseoption.exception;

public class HeartPurchaseOptionAlreadyExistsException extends RuntimeException {
    public HeartPurchaseOptionAlreadyExistsException(String productId) {
        super("이미 존재하는 상품입니다. productId=" + productId);
    }
}
