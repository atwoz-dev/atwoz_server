package deepple.deepple.community.command.domain.selfintroduction.exception;

public class InvalidSelfIntroductionTitleException extends RuntimeException {
    public InvalidSelfIntroductionTitleException() {
        super("셀프 소개의 제목을 입력해주세요.");
    }
}
