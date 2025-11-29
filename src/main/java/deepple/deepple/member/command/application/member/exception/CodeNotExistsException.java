package deepple.deepple.member.command.application.member.exception;

public class CodeNotExistsException extends RuntimeException {
    public CodeNotExistsException() {
        super("인증코드가 존재하지 않습니다.");
    }
}
