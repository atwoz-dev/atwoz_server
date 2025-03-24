package atwoz.atwoz.notification.command.infra.notification;

import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationSender;
import org.springframework.stereotype.Service;

@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification, String receiverDeviceToken) {

    }
}
