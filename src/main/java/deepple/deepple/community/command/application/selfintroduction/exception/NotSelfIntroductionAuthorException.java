package deepple.deepple.community.command.application.selfintroduction.exception;

public class NotSelfIntroductionAuthorException extends RuntimeException {
    public NotSelfIntroductionAuthorException() {
        super("해당 소개 글의 작성자가 아닙니다.");
    }
}
