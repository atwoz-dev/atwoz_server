package deepple.deepple.interview.command.domain.question.exception;

public class InvalidInterviewCategoryException extends RuntimeException {
    public InvalidInterviewCategoryException(String value) {
        super("유효하지 않은 enum 값 입니다. : " + value);
    }
}
