package deepple.deepple.community.command.domain.selfintroduction.exception;

public class InvalidSelfIntroductionContentException extends RuntimeException {
    public InvalidSelfIntroductionContentException() {
        super("셀프 소개 글은 최소 30자 이상이어야 합니다.");
    }
}
