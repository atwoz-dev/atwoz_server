package atwoz.atwoz.notification.command.application;

public class NotificationSendFailureException extends RuntimeException {
    public NotificationSendFailureException(String message) {
        super("알림 전송에 실패했습니다.");
    }
}
