package deepple.deepple.member.command.application.introduction.exception;

public class IntroducedMemberBlockedException extends RuntimeException {
    public IntroducedMemberBlockedException() {
        super("소개받는 회원이 차단한 회원입니다.");
    }
}
