package atwoz.atwoz.match.command.domain.match.exception;

public class InvalidMatchStatusChangeException extends RuntimeException {
    public InvalidMatchStatusChangeException() {
        super("대기 상태일때 매치 상태를 변경할 수 있습니다.");
    }
}
