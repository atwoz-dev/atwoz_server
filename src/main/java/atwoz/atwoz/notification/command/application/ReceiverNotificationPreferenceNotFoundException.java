package atwoz.atwoz.notification.command.application;

public class ReceiverNotificationPreferenceNotFoundException extends RuntimeException {
    public ReceiverNotificationPreferenceNotFoundException(long receiverId) {
        super("알림 수신자 id: " + receiverId + "를 찾을 수 없습니다.");
    }
}
