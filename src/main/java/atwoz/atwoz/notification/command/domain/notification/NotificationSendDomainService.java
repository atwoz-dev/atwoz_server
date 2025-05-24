package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateFactory;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSendDomainService {

    private final MessageTemplateFactory messageTemplateFactory;
    private final NotificationSender notificationSender;

    public void send(Notification notification, NotificationSetting receiverNotificationSetting) {
        createMessage(notification);
        sendIfOptedIn(notification, receiverNotificationSetting);
    }

    private void createMessage(Notification notification) {
        MessageTemplate template = messageTemplateFactory.getByNotificationType(notification.getType());
        var parameters = MessageTemplateParameters.of(notification.getSenderId(), notification.getReceiverId());
        notification.setMessage(template, parameters);
    }

    private void sendIfOptedIn(Notification notification, NotificationSetting receiverNotificationSetting) {
        if (receiverNotificationSetting.isOptedIn()) {
            notificationSender.send(notification, receiverNotificationSetting.getDeviceToken());
        }
    }
}
