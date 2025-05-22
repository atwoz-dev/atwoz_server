package atwoz.atwoz.notification.command.application;

public class NotificationSettingNotFoundException extends RuntimeException {
    public NotificationSettingNotFoundException(long memberId) {
        super("멤버 id " + memberId + " 에 대한 알림 설정을 찾을 수 없습니다.");
    }
}
