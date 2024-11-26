package awtoz.awtoz.common.auth.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("권한이 존재하지 않습니다.");
    }
}
