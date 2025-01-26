package atwoz.atwoz.member.application.exception;

public class KakaoIdAlreadyExistsException extends RuntimeException {
    public KakaoIdAlreadyExistsException() {
        super("해당 카카오 아이디를 사용하는 유저가 존재합니다.");
    }
}
