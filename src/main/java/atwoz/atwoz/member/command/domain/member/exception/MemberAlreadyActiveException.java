package atwoz.atwoz.member.command.domain.member.exception;

public class MemberAlreadyActiveException extends RuntimeException {
    public MemberAlreadyActiveException() {
        super("이미 해당 회원은 활동 상태입니다.");
    }

}
