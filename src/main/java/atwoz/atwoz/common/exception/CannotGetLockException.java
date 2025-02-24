package atwoz.atwoz.common.exception;

public class CannotGetLockException extends RuntimeException {
    public CannotGetLockException() {
        super("잠금을 획득하지 못하였습니다.");
    }
}
