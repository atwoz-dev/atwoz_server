package atwoz.atwoz.notification.command.infra.notification;

import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationSender;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    @Async
    public void send(Notification notification, String receiverDeviceToken) {
        try {
            Message message = Message.builder()
                    .putData("senderId", String.valueOf(notification.getSenderId()))
                    .putData("senderType", notification.getSenderType().toString())
                    .putData("receiverId", String.valueOf(notification.getReceiverId()))
                    .putData("notificationType", notification.getType().toString())
                    .putData("title", notification.getTitle())
                    .putData("content", notification.getContent())
                    .setToken(receiverDeviceToken)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 메시지 전송 성공, response: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage(), e);
            // TODO: 재시도 등
        }
    }
}
