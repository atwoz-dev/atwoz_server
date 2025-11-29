package deepple.deepple.member.command.application.introduction.exception;

public class MemberIntroductionAlreadyExistsException extends RuntimeException {
    public MemberIntroductionAlreadyExistsException() {
        super("이미 소개된 회원입니다.");
    }
}
