package atwoz.atwoz.notification.command.application.notification;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(long notificationId) {
        super("알림 id: " + notificationId + "를 찾을 수 없습니다.");
    }
}
