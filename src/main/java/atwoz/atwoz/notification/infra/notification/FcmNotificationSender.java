package atwoz.atwoz.notification.infra.notification;

import atwoz.atwoz.notification.command.application.NotificationSendFailureException;
import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationSender;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    public ChannelType channel() {
        return ChannelType.PUSH;
    }

    @Override
    public void send(Notification notification, List<DeviceRegistration> devices) {
        var message = MulticastMessage.builder()
            .addAllTokens(devices.stream().map(DeviceRegistration::getRegistrationToken).toList())
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
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("{}개의 FCM 알림 전송 성공", response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송 실패: {}", e.getMessage(), e);
            throw new NotificationSendFailureException();
        }
    }
}
