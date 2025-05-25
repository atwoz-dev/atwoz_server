package atwoz.atwoz.notification.command.application;

public class NotificationSendFailureException extends RuntimeException {
    public NotificationSendFailureException() {
        super("알림 전송에 실패했습니다.");
    }
}
