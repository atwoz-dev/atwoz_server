package atwoz.atwoz.member.command.application.member.exception;

public class CodeNotMatchException extends RuntimeException {
    public CodeNotMatchException() {
        super("인증번호가 일치하지 않습니다.");
    }
}
