package deepple.deepple.member.command.application.member.exception;

public class PermanentlySuspendedMemberException extends RuntimeException {
    public PermanentlySuspendedMemberException() {
        super("영구 정지된 유저입니다.");
    }
}
