package deepple.deepple.member.command.application.profileImage.exception;

public class ProfileImageMemberIdMismatchException extends RuntimeException {
    public ProfileImageMemberIdMismatchException() {
        super("프로필 이미지가 해당 유저의 소유가 아닙니다.");
    }
}
