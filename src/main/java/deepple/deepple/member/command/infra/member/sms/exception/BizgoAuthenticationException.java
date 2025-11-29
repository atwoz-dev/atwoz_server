package deepple.deepple.member.command.infra.member.sms.exception;

public class BizgoAuthenticationException extends RuntimeException {
    public BizgoAuthenticationException() {
        super("토큰 발급에 실패하였습니다.");
    }
}
