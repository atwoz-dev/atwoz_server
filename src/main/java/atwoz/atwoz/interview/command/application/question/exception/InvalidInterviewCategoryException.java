package atwoz.atwoz.interview.command.application.question.exception;

public class InvalidInterviewCategoryException extends RuntimeException {
    public InvalidInterviewCategoryException() {
        super("유효하지 않은 인터뷰 카테고리입니다.");
    }
}
