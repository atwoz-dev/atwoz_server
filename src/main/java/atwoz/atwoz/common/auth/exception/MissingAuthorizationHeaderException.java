package atwoz.atwoz.common.auth.exception;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException() {
        super("Authorization 헤더가 존재하지 않습니다.");
    }
}
