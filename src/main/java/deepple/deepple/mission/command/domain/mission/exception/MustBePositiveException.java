package deepple.deepple.mission.command.domain.mission.exception;

public class MustBePositiveException extends RuntimeException {
    public MustBePositiveException(int value) {
        super("해당 값은 0보다 커야 합니다. : " + value);
    }
}
