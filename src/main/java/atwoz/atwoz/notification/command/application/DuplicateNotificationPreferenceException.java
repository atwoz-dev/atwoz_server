package atwoz.atwoz.notification.command.application;

public class DuplicateNotificationPreferenceException extends RuntimeException {
    public DuplicateNotificationPreferenceException(long memberId) {
        super("멤버(id: " + memberId + ")에 대해 중복된 NotificationPreference를 생성할 수 없습니다.");
    }
}
