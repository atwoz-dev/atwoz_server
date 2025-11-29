package deepple.deepple.interview.command.application.answer.exception;

public class InterviewAnswerAccessDeniedException extends RuntimeException {
    public InterviewAnswerAccessDeniedException() {
        super("Access denied to the interview answer.");
    }
}
