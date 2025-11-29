package deepple.deepple.match.command.domain.match.exception;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String message) {
        super("유효하지 않은 메세지입니다 :  " + message);
    }
}
