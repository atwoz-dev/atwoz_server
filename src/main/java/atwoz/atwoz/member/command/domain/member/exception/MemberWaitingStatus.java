package atwoz.atwoz.member.command.domain.member.exception;

public class MemberWaitingStatus extends RuntimeException {
    public MemberWaitingStatus() {
        super("심사 대기중 상태입니다.");
    }
}
