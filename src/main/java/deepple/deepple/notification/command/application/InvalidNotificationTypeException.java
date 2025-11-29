package deepple.deepple.notification.command.application;

public class InvalidNotificationTypeException extends RuntimeException {
    public InvalidNotificationTypeException(String type) {
        super(type + "은 지원하지 않는 알림 타입입니다.");
    }
}
