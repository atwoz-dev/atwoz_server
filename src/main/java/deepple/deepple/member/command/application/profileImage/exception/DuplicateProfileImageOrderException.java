package deepple.deepple.member.command.application.profileImage.exception;

public class DuplicateProfileImageOrderException extends RuntimeException {
    public DuplicateProfileImageOrderException() {
        super("프로필 이미지의 순서가 중복입니다.");
    }
}
