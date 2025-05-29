package atwoz.atwoz.notification.infra;

import atwoz.atwoz.notification.command.application.NotificationSendFailureException;
import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationSender;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    public ChannelType channel() {
        return ChannelType.PUSH;
    }

    @Override
    public void send(Notification notification, DeviceRegistration deviceRegistration) {
        var message = Message.builder()
            .setToken(deviceRegistration.getRegistrationToken())
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(notification.getTitle())
                    .setBody(notification.getBody())
                    .build()
            )
            .putData("senderId", String.valueOf(notification.getSenderId()))
            .putData("senderType", notification.getSenderType().toString())
            .putData("receiverId", String.valueOf(notification.getReceiverId()))
            .putData("notificationType", notification.getType().toString())
            .build();

        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공: messageId={}", messageId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 전송 실패: {}", e.getMessage(), e);
            throw new NotificationSendFailureException(e.getMessage());
        }
    }
}
