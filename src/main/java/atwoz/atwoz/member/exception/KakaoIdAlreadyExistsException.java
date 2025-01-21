package atwoz.atwoz.member.exception;

public class KakaoIdAlreadyExistsException extends RuntimeException {
    public KakaoIdAlreadyExistsException() {
        super("해당 카카오 ID가 이미 등록되었습니다.");
    }
}
