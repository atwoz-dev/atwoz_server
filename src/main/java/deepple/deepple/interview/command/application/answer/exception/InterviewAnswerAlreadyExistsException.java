package deepple.deepple.interview.command.application.answer.exception;

public class InterviewAnswerAlreadyExistsException extends RuntimeException {
    public InterviewAnswerAlreadyExistsException() {
        super("이미 인터뷰 답변이 존재합니다.");
    }
}
