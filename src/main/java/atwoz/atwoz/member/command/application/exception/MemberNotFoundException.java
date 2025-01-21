package atwoz.atwoz.member.command.application.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("해당 멤버를 찾을 수 없습니다.");
    }
}
