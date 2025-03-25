package atwoz.atwoz.notification.command.domain.notification.message;

import org.springframework.stereotype.Component;

@Component
public class MessageTemplateFactory {

    public MessageTemplate create(MessageTemplateParameters parameters) {
        switch (parameters.getNotificationType()) {
            case SOCIAL_MATCH_REQUESTED -> {
                return MatchRequestedMessageTemplate.from(parameters.getReceiverName());
            }
            case ADMIN_INAPPROPRIATE_CONTENT -> {
                return new InappropriateContentMessageTemplate();
            }
            default -> {
                return new DefaultMessageTemplate();
            }
        }
    }
}
