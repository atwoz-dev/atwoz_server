package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageTemplateFactory {

    private final Map<NotificationType, MessageTemplate> messageTemplateMap;

    public MessageTemplateFactory(List<MessageTemplate> messageTemplates) {
        messageTemplateMap = messageTemplates.stream()
            .collect(Collectors.toMap(MessageTemplate::getNotificationType, s -> s));
    }

    public MessageTemplate getByNotificationType(NotificationType notificationType) {
        return messageTemplateMap.getOrDefault(notificationType, new DefaultMessageTemplate());
    }
}
