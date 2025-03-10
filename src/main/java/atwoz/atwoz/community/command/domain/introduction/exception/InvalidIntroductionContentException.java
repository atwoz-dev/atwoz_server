package atwoz.atwoz.community.command.domain.introduction.exception;

public class InvalidIntroductionContentException extends RuntimeException {
    public InvalidIntroductionContentException() {
        super("셀프 소개 글은 최소 30자 이상이어야 합니다.");
    }
}
