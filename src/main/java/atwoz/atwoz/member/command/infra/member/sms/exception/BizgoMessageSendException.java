package atwoz.atwoz.member.command.infra.member.sms.exception;

public class BizgoMessageSendException extends RuntimeException {
    public BizgoMessageSendException() {
        super("메세지 전송 요청에 실패하였습니다.");
    }
}
