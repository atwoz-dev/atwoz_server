package deepple.deepple.member.command.application.member.exception;

public class MemberDeletedException extends RuntimeException {
    public MemberDeletedException() {
        super("삭제된 회원입니다.");
    }
}
