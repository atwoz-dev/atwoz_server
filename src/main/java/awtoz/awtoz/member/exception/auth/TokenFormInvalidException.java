package awtoz.awtoz.member.exception.auth;

public class TokenFormInvalidException extends RuntimeException {
    public TokenFormInvalidException() {
        super("토큰 형식이 유효하지 않습니다.");
    }
}
