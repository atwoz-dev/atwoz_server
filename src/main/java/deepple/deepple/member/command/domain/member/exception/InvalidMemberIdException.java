package deepple.deepple.member.command.domain.member.exception;

public class InvalidMemberIdException extends RuntimeException {
    public InvalidMemberIdException() {
        super("유효하지 않은 멤버 아이디입니다.");
    }
}
