package atwoz.atwoz.notification.command.application.notification;

public class NotificationReceiverNotFoundException extends RuntimeException {
    public NotificationReceiverNotFoundException(long receiverId) {
        super("알림 수신자 id: " + receiverId + "를 찾을 수 없습니다.");
    }
}
