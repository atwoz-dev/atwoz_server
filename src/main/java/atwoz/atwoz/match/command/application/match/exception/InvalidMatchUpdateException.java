package atwoz.atwoz.match.command.application.match.exception;

public class InvalidMatchUpdateException extends RuntimeException {
    public InvalidMatchUpdateException() {
        super("해당 상태로 변경이 불가능한 매치입니다.");
    }
}
