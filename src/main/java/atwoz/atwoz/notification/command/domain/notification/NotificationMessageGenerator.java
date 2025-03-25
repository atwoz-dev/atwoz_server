package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.strategy.MatchRequestedMessageStrategy;
import atwoz.atwoz.notification.command.domain.notification.strategy.NotificationMessageStrategy;

public class NotificationMessageGenerator {

    public NotificationMessageStrategy create(NotificationType notificationType, String receiverName) {
        switch (notificationType) {
            case MATCH_REQUESTED -> {
                return MatchRequestedMessageStrategy.from(receiverName);
            }
            default -> throw new IllegalArgumentException(notificationType + "는 지원하지 않는 알림 타입입니다.");
        }
    }
}
