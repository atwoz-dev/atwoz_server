package atwoz.atwoz.member.command.application.profileImage.exception;

public class InvalidImageFileException extends RuntimeException {
    public InvalidImageFileException() {
        super("해당 파일은 이미지가 아닙니다.");
    }
}
