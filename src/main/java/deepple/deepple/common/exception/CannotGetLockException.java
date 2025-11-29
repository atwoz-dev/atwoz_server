package deepple.deepple.common.exception;

public class CannotGetLockException extends RuntimeException {
    public CannotGetLockException(Exception e) {
        super("잠금을 획득하지 못하였습니다.", e);
    }
}
