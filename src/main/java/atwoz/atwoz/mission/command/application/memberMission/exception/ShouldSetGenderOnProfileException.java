package atwoz.atwoz.mission.command.application.memberMission.exception;

public class ShouldSetGenderOnProfileException extends RuntimeException {
    public ShouldSetGenderOnProfileException(Long id) {
        super("성별 설정이 필요합니다. id : " + id);
    }
}
