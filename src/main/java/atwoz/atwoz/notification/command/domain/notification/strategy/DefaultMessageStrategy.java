package atwoz.atwoz.notification.command.domain.notification.strategy;

public class DefaultMessageStrategy implements NotificationMessageStrategy {

    @Override
    public String createTitle() {
        return "알림이 전송되었습니다.";
    }

    @Override
    public String createContent() {
        return null;
    }
}
