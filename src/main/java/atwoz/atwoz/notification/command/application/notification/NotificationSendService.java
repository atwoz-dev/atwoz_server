package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.NotificationSendDomainService;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSettingCommandRepository;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.notification.command.application.notification.NotificationMapper.toNotification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final NotificationCommandRepository notificationCommandRepository;
    private final NotificationSettingCommandRepository notificationSettingCommandRepository;
    private final NotificationSendDomainService notificationSendDomainService;

    @Transactional
    public void send(NotificationRequest request) {
        Notification notification = toNotification(request);
        NotificationSetting receiverNotificationSetting = getNotificationSetting(notification.getReceiverId());
        notificationSendDomainService.send(notification, receiverNotificationSetting);
        log.info("알림 발송 완료. receiverId={}, notificationType={}", notification.getReceiverId(), notification.getType());

        notificationCommandRepository.save(notification);
    }

    private NotificationSetting getNotificationSetting(long receiverId) {
        return notificationSettingCommandRepository.findByMemberId(receiverId)
            .orElseThrow(() -> new ReceiverNotificationSettingNotFoundException(receiverId));
    }
}
