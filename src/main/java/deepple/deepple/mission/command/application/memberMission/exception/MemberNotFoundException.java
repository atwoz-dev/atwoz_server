package deepple.deepple.mission.command.application.memberMission.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long id) {
        super("해당 아이디의 멤버를 찾을 수 없습니다. : " + id);
    }
}
