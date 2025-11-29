package deepple.deepple.heart.command.domain.hearttransaction.exception;

public class InsufficientHeartBalanceException extends RuntimeException {
    public InsufficientHeartBalanceException() {
        super("잔여 하트가 부족합니다.");
    }
}
