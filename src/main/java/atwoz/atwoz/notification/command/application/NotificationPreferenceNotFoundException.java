package atwoz.atwoz.notification.command.application;

public class NotificationPreferenceNotFoundException extends RuntimeException {
    public NotificationPreferenceNotFoundException(long memberId) {
        super("멤버 id " + memberId + " 에 대한 NotificationPreference를 찾을 수 없습니다.");
    }
}
