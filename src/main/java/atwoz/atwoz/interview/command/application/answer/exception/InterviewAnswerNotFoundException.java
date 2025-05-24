package atwoz.atwoz.interview.command.application.answer.exception;

public class InterviewAnswerNotFoundException extends RuntimeException {
    public InterviewAnswerNotFoundException() {
        super("Interview answer not found.");
    }
}
