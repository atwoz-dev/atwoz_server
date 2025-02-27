package atwoz.atwoz.match.command.application.match.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException() {
        super("해당 매치를 찾을 수 없습니다.");
    }
}
