package deepple.deepple.match.command.application.match.exception;

public class ExistsMatchException extends RuntimeException {
    public ExistsMatchException() {
        super("이미 상대방과의 매치가 존재합니다.");
    }
}
