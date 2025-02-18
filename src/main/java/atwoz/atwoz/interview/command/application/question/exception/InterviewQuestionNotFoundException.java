package atwoz.atwoz.interview.command.application.question.exception;

public class InterviewQuestionNotFoundException extends RuntimeException {
    public InterviewQuestionNotFoundException() {
        super("해당 질문이 존재하지 않습니다.");
    }
}
