package atwoz.atwoz.notification.command.domain.notification.strategy;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class DefaultNotificationMessageStrategy implements NotificationMessageStrategy {

    @Override
    public NotificationType getNotificationType() {
        return null;
    }

    @Override
    public String generateTitle(String receiverName) {
        return "알림이 도착했습니다.";
    }

    @Override
    public String generateContent() {
        return "";
    }
}