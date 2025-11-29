package deepple.deepple.mission.command.domain.mission.exception;

public class InvalidMissionEnumValueException extends RuntimeException {
    public InvalidMissionEnumValueException(String value) {
        super("Enum에 해당하지 않은 값입니다. : " + value);
    }
}
