package deepple.deepple.admin.command.domain.admin.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
