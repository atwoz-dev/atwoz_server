package deepple.deepple.member.command.application.introduction.exception;

public class IntroducedMemberNotFoundException extends RuntimeException {
    public IntroducedMemberNotFoundException() {
        super("소개된 멤버가 존재하지 않습니다.");
    }
}
