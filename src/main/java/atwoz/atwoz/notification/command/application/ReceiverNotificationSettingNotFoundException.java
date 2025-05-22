package atwoz.atwoz.notification.command.application;

public class ReceiverNotificationSettingNotFoundException extends RuntimeException {
    public ReceiverNotificationSettingNotFoundException(long receiverId) {
        super("알림 수신자 id: " + receiverId + "를 찾을 수 없습니다.");
    }
}
