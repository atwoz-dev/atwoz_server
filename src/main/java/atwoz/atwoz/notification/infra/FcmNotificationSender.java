package atwoz.atwoz.notification.infra;

import atwoz.atwoz.notification.command.application.FcmException;
import atwoz.atwoz.notification.command.application.NotificationSendFailedException;
import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationSender;
import atwoz.atwoz.notification.command.infra.FcmResilienceConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    @Retry(name = FcmResilienceConfig.RETRY_POLICY_NAME)
    @CircuitBreaker(name = FcmResilienceConfig.CIRCUIT_BREAKER_POLICY_NAME, fallbackMethod = "sendFallback")
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
            log.info("FCM 전송 성공: messageId={}, receiverId={}", messageId, notification.getReceiverId());
        } catch (FirebaseMessagingException e) {
            throw new FcmException(e);
        }
    }

    public void sendFallback(Notification notification, DeviceRegistration deviceRegistration, Throwable throwable) {
        String errorType = throwable.getClass().getSimpleName();
        String errorMessage = throwable.getMessage();

        // FcmException인 경우 원본 FirebaseMessagingException의 에러 코드 로깅
        if (throwable instanceof FcmException fcmEx) {
            FirebaseMessagingException fme = fcmEx.getCause();
            log.error("FCM 전송 실패: receiverId={}, errorType={}, errorCode={}, message={}",
                notification.getReceiverId(), errorType, fme.getMessagingErrorCode(), errorMessage, throwable);
        } else {
            log.error("FCM 전송 실패: receiverId={}, errorType={}, message={}",
                notification.getReceiverId(), errorType, errorMessage, throwable);
        }

        throw new NotificationSendFailedException(throwable);
    }
}
