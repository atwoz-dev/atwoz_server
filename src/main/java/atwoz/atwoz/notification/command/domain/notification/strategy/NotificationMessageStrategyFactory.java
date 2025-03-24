package atwoz.atwoz.notification.command.domain.notification.strategy;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationMessageStrategyFactory {

    private final Map<NotificationType, NotificationMessageStrategy> strategyMap;
    private final NotificationMessageStrategy defaultStrategy;

    public NotificationMessageStrategyFactory(List<NotificationMessageStrategy> strategies) {
        // Default 전략을 구분하기 위해 instanceOf를 활용합니다.
        this.defaultStrategy = strategies.stream()
                .filter(strategy -> strategy instanceof DefaultNotificationMessageStrategy)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Default NotificationMessageStrategy not found"));

        this.strategyMap = strategies.stream()
                .filter(strategy -> !(strategy instanceof DefaultNotificationMessageStrategy))
                .collect(Collectors.toMap(NotificationMessageStrategy::getNotificationType, Function.identity()));
    }

    public NotificationMessageStrategy getStrategy(NotificationType type) {
        return strategyMap.getOrDefault(type, defaultStrategy);
    }
}