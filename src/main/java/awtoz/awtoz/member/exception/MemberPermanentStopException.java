package awtoz.awtoz.member.exception;

public class MemberPermanentStopException extends RuntimeException {
    public MemberPermanentStopException() {
        super("영구 정지된 유저입니다.");
    }
}
