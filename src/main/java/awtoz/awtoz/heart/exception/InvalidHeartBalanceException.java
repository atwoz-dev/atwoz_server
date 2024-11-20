package awtoz.awtoz.heart.exception;

public class InvalidHeartBalanceException extends RuntimeException {
    public InvalidHeartBalanceException() {
        super("하트 잔액은 0 이상의 값이어야 합니다.");
    }
}
