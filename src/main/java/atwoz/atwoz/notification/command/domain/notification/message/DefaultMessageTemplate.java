package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessageTemplate implements MessageTemplate {

    @Override
    public NotificationType getNotificationType() {
        return null;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        return "제목";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return "내용";
    }
}
