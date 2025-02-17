package atwoz.atwoz.interview.command.application.answer.exception;

public class InterviewAnserAlreadyExistsException extends RuntimeException {
    public InterviewAnserAlreadyExistsException() {
        super("이미 인터뷰 답변이 존재합니다.");
    }
}
