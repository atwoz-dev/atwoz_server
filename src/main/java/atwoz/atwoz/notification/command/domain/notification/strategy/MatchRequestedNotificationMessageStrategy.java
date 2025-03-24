package atwoz.atwoz.notification.command.domain.notification.strategy;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class MatchRequestedNotificationMessageStrategy implements NotificationMessageStrategy {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_REQUESTED;
    }

    @Override
    public String generateTitle(String receiverName) {
        return String.format("%s님께 매치가 요청되었습니다.", receiverName);
    }

    @Override
    public String generateContent() {
        return "매치 요청에 대한 상세 내용입니다.";
    }
}