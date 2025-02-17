package atwoz.atwoz.interview.command.domain.question.exception;

public class InvalidInterviewQuestionContentException extends RuntimeException {
    public InvalidInterviewQuestionContentException() {
        super("인터뷰 질문의 내용이 유효하지 않습니다.");
    }
}
