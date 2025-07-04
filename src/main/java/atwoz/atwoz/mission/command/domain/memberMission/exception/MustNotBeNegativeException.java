package atwoz.atwoz.mission.command.domain.memberMission.exception;

public class MustNotBeNegativeException extends RuntimeException {
    public MustNotBeNegativeException(int value) {
        super("해당 값은 음수가 될 수 없습니다. : " + value);
    }
}
