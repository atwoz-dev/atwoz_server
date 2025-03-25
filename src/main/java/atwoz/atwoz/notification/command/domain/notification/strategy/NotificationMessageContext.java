package atwoz.atwoz.notification.command.domain.notification.strategy;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotificationMessageContext {

    public String createTitle(NotificationMessageStrategy strategy) {
        return strategy.createTitle();
    }

    public String createContent(NotificationMessageStrategy strategy) {
        return strategy.createContent();
    }
}
