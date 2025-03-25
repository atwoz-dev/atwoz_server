package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.strategy.DefaultMessageStrategy;
import atwoz.atwoz.notification.command.domain.notification.strategy.MatchRequestedMessageStrategy;
import atwoz.atwoz.notification.command.domain.notification.strategy.NotificationMessageStrategy;

public class NotificationMessageGenerator {

    public NotificationMessageStrategy create(NotificationType notificationType, String receiverName) {
        switch (notificationType) {
            case MATCH_REQUESTED -> {
                return MatchRequestedMessageStrategy.from(receiverName);
            }
            default -> {
                return new DefaultMessageStrategy();
            }
        }
    }
}
