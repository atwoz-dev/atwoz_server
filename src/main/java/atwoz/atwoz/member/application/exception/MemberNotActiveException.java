package atwoz.atwoz.member.application.exception;

public class MemberNotActiveException extends RuntimeException {
    public MemberNotActiveException() {
        super("활동중 상태가 아닙니다.");
    }
}
