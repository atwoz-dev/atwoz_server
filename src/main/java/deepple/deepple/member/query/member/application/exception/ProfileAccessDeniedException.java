package deepple.deepple.member.query.member.application.exception;

public class ProfileAccessDeniedException extends RuntimeException {
    public ProfileAccessDeniedException() {
        super("해당 프로필에 접근할 권한이 존재하지 않습니다.");
    }
}
