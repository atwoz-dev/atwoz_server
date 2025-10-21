package atwoz.atwoz.member.command.application.member.exception;

public class TemporarilySuspendedMemberException extends RuntimeException {
    public TemporarilySuspendedMemberException() {
        super("일시 정지된 유저입니다.");
    }
}
