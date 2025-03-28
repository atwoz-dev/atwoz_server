package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageTemplateFactory {

    private final Map<NotificationType, MessageTemplate> notificationTypeMessageTemplateMap;

    public MessageTemplateFactory(List<MessageTemplate> templates) {
        this.notificationTypeMessageTemplateMap = templates.stream()
                .collect(Collectors.toMap(MessageTemplate::getNotificationType, s -> s));
    }

    public MessageTemplate getTemplateByNotificationType(NotificationType notificationType) {
        return notificationTypeMessageTemplateMap.get(notificationType);
    }
}
