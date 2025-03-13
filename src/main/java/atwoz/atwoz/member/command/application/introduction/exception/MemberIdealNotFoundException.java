package atwoz.atwoz.member.command.application.introduction.exception;

public class MemberIdealNotFoundException extends RuntimeException {
    public MemberIdealNotFoundException() {
        super("멤버의 이상형이 존재하지 않습니다.");
    }
}
