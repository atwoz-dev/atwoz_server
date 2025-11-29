package deepple.deepple.interview.command.application.question.exception;

public class InterviewQuestionIsNotPublicException extends RuntimeException {
    public InterviewQuestionIsNotPublicException() {
        super("해당 인터뷰 질문은 공개되지 않았습니다.");
    }
}
