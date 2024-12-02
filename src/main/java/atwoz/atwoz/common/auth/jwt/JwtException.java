package atwoz.atwoz.common.auth.jwt;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super("JWT 파싱 중 오류가 발생했습니다: " + message);
    }
}
