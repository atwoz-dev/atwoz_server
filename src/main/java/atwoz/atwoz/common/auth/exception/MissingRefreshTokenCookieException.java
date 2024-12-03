package atwoz.atwoz.common.auth.exception;

public class MissingRefreshTokenCookieException extends RuntimeException {
    public MissingRefreshTokenCookieException() {
        super("Refresh token 쿠키가 존재하지 않습니다.");
    }
}
