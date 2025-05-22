package atwoz.atwoz.notification.command.domain;

import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateFactory;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSendDomainService {

    private final MessageTemplateFactory messageTemplateFactory;
    private final NotificationSender notificationSender;

    public void send(Notification notification, NotificationPreference receiverNotificationPreference) {
        createMessage(notification);
        sendIfOptedIn(notification, receiverNotificationPreference);
    }

    private void createMessage(Notification notification) {
        MessageTemplate template = messageTemplateFactory.getByNotificationType(notification.getType());
        var parameters = MessageTemplateParameters.of(notification.getSenderId(), notification.getReceiverId());
        notification.setMessage(template, parameters);
    }

    private void sendIfOptedIn(Notification notification, NotificationPreference receiverNotificationPreference) {
        if (receiverNotificationPreference.isOptedIn()) {
            notificationSender.send(notification, receiverNotificationPreference.getDeviceToken());
        }
    }
}
