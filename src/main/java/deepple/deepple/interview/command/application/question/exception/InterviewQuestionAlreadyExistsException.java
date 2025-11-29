package deepple.deepple.interview.command.application.question.exception;

public class InterviewQuestionAlreadyExistsException extends RuntimeException {
    public InterviewQuestionAlreadyExistsException() {
        super("이미 존재하는 질문입니다.");
    }
}
