package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final NotificationCommandRepository notificationCommandRepository;
    private final NotificationPreferenceCommandRepository notificationPreferenceCommandRepository;
    private final NotificationTemplateCommandRepository notificationTemplateCommandRepository;
    private final DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;
    private final NotificationSenderResolver notificationSenderResolver;

    @Transactional
    public void send(NotificationSendRequest request) {
        var template = getTemplate(request.notificationType());
        String title = template.generateTitle(request.params());
        String body = template.generateBody(request.params());
        var notification = Notification.create(
            request.senderType(),
            request.senderId(),
            request.receiverId(),
            request.notificationType(),
            title,
            body
        );

        var preference = getPreference(request.receiverId());
        if (!preference.canReceive(notification.getType())) {
            return;
        }

        var devices = deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(request.receiverId());
        notificationSenderResolver.resolve(request.channelType())
            .ifPresentOrElse(
                sender -> {
                    try {
                        sender.send(notification, devices);
                        notification.markAsSent();
                    } catch (NotificationSendFailureException e) {
                        log.error("receiverId={}에게 {}알림 전송 실패", request.receiverId(), notification.getType(), e);
                        notification.markAsFailedDueToException();
                    }
                },
                () -> {
                    log.warn("{}는 지원하지 않는 채널입니다. receiverId={}", request.channelType(), request.receiverId());
                    notification.markAsFailedDueToUnsupportedChannel();
                }
            );

        notificationCommandRepository.save(notification);
    }

    private NotificationTemplate getTemplate(NotificationType type) {
        return notificationTemplateCommandRepository.findByType(type)
            .orElseThrow(() -> new InvalidNotificationTypeException(type.toString()));
    }

    private NotificationPreference getPreference(long receiverId) {
        return notificationPreferenceCommandRepository.findByMemberId(receiverId)
            .orElseThrow(() -> new ReceiverNotificationPreferenceNotFoundException(receiverId));
    }
}
