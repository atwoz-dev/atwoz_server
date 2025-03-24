package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.strategy.NotificationMessageStrategyFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageGenerator {

    private final NotificationMessageStrategyFactory strategyFactory;

    public NotificationMessageGenerator(NotificationMessageStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public String generateTitle(NotificationType type, String receiverName) {
        return strategyFactory.getStrategy(type).generateTitle(receiverName);
    }

    public String generateContent(NotificationType type) {
        return strategyFactory.getStrategy(type).generateContent();
    }
}