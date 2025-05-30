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
    private final NotificationPreferenceCommandRepository notificationPreferenceRepository;
    private final NotificationTemplateCommandRepository notificationTemplateRepository;
    private final DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;
    private final NotificationSenderResolver notificationSenderResolver;

    @Transactional
    public void send(NotificationSendRequest request) {
        var notification = buildNotification(request);

        if (!canSendByPreference(notification)) {
            notification.markAsRejectedByPreference();
            save(notification);
            return;
        }

        var deviceRegistration = getReceiverDeviceRegistration(notification.getReceiverId());

        notificationSenderResolver.resolve(request.channelType())
            .ifPresentOrElse(
                sender -> dispatch(sender, notification, deviceRegistration),
                () -> handleUnsupportedChannel(notification, request)
            );

        save(notification);
    }

    private Notification buildNotification(NotificationSendRequest request) {
        NotificationTemplate template = notificationTemplateRepository.findByType(request.notificationType())
            .orElseThrow(() -> new InvalidNotificationTypeException(request.notificationType().name()));

        String title = template.generateTitle(request.params());
        String body = template.generateBody(request.params());

        return Notification.create(
            request.senderType(),
            request.senderId(),
            request.receiverId(),
            request.notificationType(),
            title, body
        );
    }

    private boolean canSendByPreference(Notification notification) {
        NotificationPreference pref = notificationPreferenceRepository.findByMemberId(notification.getReceiverId())
            .orElseThrow(() -> new ReceiverNotificationPreferenceNotFoundException(notification.getReceiverId()));
        return pref.canReceive(notification.getType());
    }

    private DeviceRegistration getReceiverDeviceRegistration(long receiverId) {
        return deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(receiverId)
            .orElseThrow(() -> new DeviceRegistrationNotFoundException(receiverId));
    }

    private void dispatch(NotificationSender sender, Notification notification, DeviceRegistration deviceRegistration) {
        try {
            sender.send(notification, deviceRegistration);
            notification.markAsSent();
        } catch (NotificationSendFailureException e) {
            log.error("receiverId={} 알림 전송 중 에러", notification.getReceiverId(), e);
            notification.markAsFailedDueToException();
        }
    }

    private void handleUnsupportedChannel(Notification notification, NotificationSendRequest request) {
        log.warn("지원하지 않는 채널({}) 요청: receiverId={}", request.channelType(), request.receiverId());
        notification.markAsFailedDueToUnsupportedChannel();
    }

    private void save(Notification notification) {
        notificationCommandRepository.save(notification);
    }
}
