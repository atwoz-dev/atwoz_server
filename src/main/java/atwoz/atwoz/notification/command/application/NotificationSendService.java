package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.*;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.notification.command.application.NotificationMapper.toNotification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final NotificationCommandRepository notificationCommandRepository;
    private final NotificationPreferenceCommandRepository notificationPreferenceCommandRepository;
    private final NotificationSendDomainService notificationSendDomainService;

    @Transactional
    public void send(NotificationRequest request) {
        Notification notification = toNotification(request);
        NotificationPreference receiverNotificationPreference = getNotificationSetting(notification.getReceiverId());
        notificationSendDomainService.send(notification, receiverNotificationPreference);
        log.info("알림 발송 완료. receiverId={}, notificationType={}", notification.getReceiverId(), notification.getType());

        notificationCommandRepository.save(notification);
    }

    private NotificationPreference getNotificationSetting(long receiverId) {
        return notificationPreferenceCommandRepository.findByMemberId(receiverId)
            .orElseThrow(() -> new ReceiverNotificationSettingNotFoundException(receiverId));
    }
}
