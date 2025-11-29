package deepple.deepple.member.command.application.introduction.exception;

public class IntroducedMemberNotActiveException extends RuntimeException {
    public IntroducedMemberNotActiveException() {
        super("소개된 멤버가 Active 상태가 아닙니다.");
    }
}
