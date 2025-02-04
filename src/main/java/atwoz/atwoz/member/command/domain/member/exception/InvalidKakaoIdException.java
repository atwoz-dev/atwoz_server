package atwoz.atwoz.member.command.domain.member.exception;

public class InvalidKakaoIdException extends RuntimeException {
    public InvalidKakaoIdException() {
        super("유효하지 않은 카카오 아이디입니다.");
    }
}
