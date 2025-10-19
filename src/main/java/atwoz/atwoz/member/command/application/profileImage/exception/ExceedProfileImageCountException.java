package atwoz.atwoz.member.command.application.profileImage.exception;

public class ExceedProfileImageCountException extends RuntimeException {
    public ExceedProfileImageCountException(int count) {
        super("프로필 이미지는 6개를 초과할 수 없습니다. 요청 : " + count);
    }
}
