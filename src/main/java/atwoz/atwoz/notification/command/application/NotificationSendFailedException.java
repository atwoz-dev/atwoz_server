package atwoz.atwoz.notification.command.application;

public class NotificationSendFailedException extends RuntimeException {
    public NotificationSendFailedException(Throwable cause) {
        super("알림 전송에 실패했습니다.", cause);
    }
}