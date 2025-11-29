package deepple.deepple.member.command.domain.member.exception;

public class InvalidNicknameException extends RuntimeException {
    public InvalidNicknameException() {
        super("유효하지 않은 닉네임입니다.");
    }
}
