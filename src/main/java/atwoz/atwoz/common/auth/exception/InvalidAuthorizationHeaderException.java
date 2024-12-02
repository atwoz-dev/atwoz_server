package atwoz.atwoz.common.auth.exception;

public class InvalidAuthorizationHeaderException extends RuntimeException {
    public InvalidAuthorizationHeaderException() {
        super("유효하지 않은 Authorization 헤더 형식입니다.");
    }
}
