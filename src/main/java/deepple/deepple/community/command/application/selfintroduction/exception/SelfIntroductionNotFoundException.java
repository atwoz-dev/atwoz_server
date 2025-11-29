package deepple.deepple.community.command.application.selfintroduction.exception;

public class SelfIntroductionNotFoundException extends RuntimeException {
    public SelfIntroductionNotFoundException() {
        super("해당 셀프 소개글이 존재하지 않습니다.");
    }
}
