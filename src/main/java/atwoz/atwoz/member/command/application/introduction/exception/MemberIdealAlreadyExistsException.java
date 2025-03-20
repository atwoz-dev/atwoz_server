package atwoz.atwoz.member.command.application.introduction.exception;

public class MemberIdealAlreadyExistsException extends RuntimeException {
    public MemberIdealAlreadyExistsException(long memberId) {
        super("멤버(id: " + memberId + ")의 Ideal이 이미 생성되어 있습니다.");
    }
}
