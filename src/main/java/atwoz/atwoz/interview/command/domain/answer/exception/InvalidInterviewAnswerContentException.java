package atwoz.atwoz.interview.command.domain.answer.exception;

public class InvalidInterviewAnswerContentException extends RuntimeException {
    public InvalidInterviewAnswerContentException() {
        super("인터뷰 답변의 내용이 유효하지 않습니다.");
    }
}
