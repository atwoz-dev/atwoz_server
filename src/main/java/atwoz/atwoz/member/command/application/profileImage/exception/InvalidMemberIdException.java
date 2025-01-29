package atwoz.atwoz.member.command.application.profileImage.exception;

public class InvalidMemberIdException extends RuntimeException {
    public InvalidMemberIdException() {
        super("멤버 ID는 NULL이 될 수 없습니다.");
    }
}
