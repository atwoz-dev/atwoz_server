package atwoz.atwoz.member.command.application.profileImage.exception;

public class ProfileImageNotFoundException extends RuntimeException {
    public ProfileImageNotFoundException() {
        super("해당 프로필 이미지가 존재하지 않습니다.");
    }
}
