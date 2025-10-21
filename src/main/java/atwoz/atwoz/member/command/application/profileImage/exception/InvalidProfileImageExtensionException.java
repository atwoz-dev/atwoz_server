package atwoz.atwoz.member.command.application.profileImage.exception;

public class InvalidProfileImageExtensionException extends RuntimeException {
    public InvalidProfileImageExtensionException(String extension) {
        super("유효하지 않은 확장자입니다 : " + extension);
    }
}
