package atwoz.atwoz.profileimage.exception;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException() {
        super("유효하지 않은 순서입니다.");
    }
}