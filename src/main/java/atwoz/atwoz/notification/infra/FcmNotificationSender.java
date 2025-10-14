package atwoz.atwoz.notification.infra;

import atwoz.atwoz.notification.command.application.NotificationSendFailureException;
import atwoz.atwoz.notification.command.domain.*;
import atwoz.atwoz.notification.command.infra.FcmResilienceConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmNotificationSender implements NotificationSender {

    private final NotificationCommandRepository notificationCommandRepository;

    @Override
    public ChannelType channel() {
        return ChannelType.PUSH;
    }

    @Override
    @Retry(name = FcmResilienceConfig.RETRY_POLICY_NAME)
    @CircuitBreaker(name = FcmResilienceConfig.CIRCUIT_BREAKER_POLICY_NAME, fallbackMethod = "sendFallback")
    public void send(Notification notification, DeviceRegistration deviceRegistration) {
        sendFcmMessage(notification, deviceRegistration);
    }

    public void sendFallback(Notification notification, DeviceRegistration deviceRegistration, Exception exception) {
        log.error("FCM 전송 최종 실패 - Fallback 실행: receiverId={}, error={}",
            notification.getReceiverId(), exception.getMessage(), exception);

        // 실패 상태로 DB 저장 (기존 Service 로직을 여기로 이동)
        var failedNotification = Notification.createFailed(
            notification.getSenderType(),
            notification.getSenderId(),
            notification.getReceiverId(),
            notification.getType(),
            notification.getTitle(),
            notification.getBody(),
            NotificationStatus.FAILED_EXCEPTION
        );
        notificationCommandRepository.save(failedNotification);

        // Fallback에서는 예외를 발생시키지 않음 (graceful degradation)
        log.info("실패한 알림을 DB에 저장했습니다: receiverId={}", notification.getReceiverId());
    }

    private void sendFcmMessage(Notification notification, DeviceRegistration deviceRegistration) {
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
            log.error("FCM 전송 실패: receiverId={}, error={}", notification.getReceiverId(), e.getMessage());
            throw new NotificationSendFailureException(e.getMessage());
        }
    }
}
