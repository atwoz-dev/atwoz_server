package atwoz.atwoz.member.command.application.member.exception;

public class MemberWaitingStatusException extends RuntimeException {
    public MemberWaitingStatusException() {
        super("심사 대기중 상태입니다.");
    }
}
