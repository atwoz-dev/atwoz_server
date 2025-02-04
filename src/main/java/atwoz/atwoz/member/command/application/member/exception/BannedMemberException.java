package atwoz.atwoz.member.command.application.member.exception;

public class BannedMemberException extends RuntimeException {
    public BannedMemberException() {
        super("영구 정지된 유저입니다.");
    }
}
