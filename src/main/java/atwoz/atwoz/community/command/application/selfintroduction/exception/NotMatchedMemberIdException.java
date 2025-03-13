package atwoz.atwoz.community.command.application.selfintroduction.exception;

public class NotMatchedMemberIdException extends RuntimeException {
    public NotMatchedMemberIdException() {
        super("해당 소개 글의 작성자가 아닙니다.");
    }
}
