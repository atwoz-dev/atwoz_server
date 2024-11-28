package atwoz.atwoz.common.auth.infra.exception;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException() {
        super("유효하지 않은 토큰입니다.");
    }

}
