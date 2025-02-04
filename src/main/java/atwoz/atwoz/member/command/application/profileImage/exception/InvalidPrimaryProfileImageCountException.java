package atwoz.atwoz.member.command.application.profileImage.exception;

public class InvalidPrimaryProfileImageCountException extends RuntimeException {
    public InvalidPrimaryProfileImageCountException() {
        super("요청에 대표 이미지가 2개 이상 포함됩니다.");
    }
}
