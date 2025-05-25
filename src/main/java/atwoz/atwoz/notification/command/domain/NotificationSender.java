package atwoz.atwoz.notification.command.domain;

import java.util.List;

public interface NotificationSender {
    ChannelType channel();

    void send(Notification notification, List<DeviceRegistration> devices);
}
