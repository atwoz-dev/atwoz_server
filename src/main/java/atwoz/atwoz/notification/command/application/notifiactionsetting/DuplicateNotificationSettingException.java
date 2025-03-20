package atwoz.atwoz.notification.command.application.notifiactionsetting;

public class DuplicateNotificationSettingException extends RuntimeException {
    public DuplicateNotificationSettingException(long memberId) {
        super("멤버(id: " + memberId + ")에 대해 중복된 NotificationSetting을 생성할 수 없습니다.");
    }
}
