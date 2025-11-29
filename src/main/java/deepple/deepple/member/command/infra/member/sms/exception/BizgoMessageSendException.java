package deepple.deepple.member.command.infra.member.sms.exception;

import lombok.Getter;

@Getter
public class BizgoMessageSendException extends RuntimeException {
    private int statusCode;
    
    public BizgoMessageSendException(int statusCode) {
        super("메세지 전송 요청에 실패하였습니다.");
        this.statusCode = statusCode;
    }
}
