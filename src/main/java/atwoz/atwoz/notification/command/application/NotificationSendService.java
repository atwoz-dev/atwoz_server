package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        if (cantSendByPreference(notification)) {
            notification.markAsRejectedByPreference();
            save(notification);
            return;
        }

        List<DeviceRegistration> devices = fetchActiveDevices(notification.getReceiverId());

        notificationSenderResolver.resolve(request.channelType())
            .ifPresentOrElse(
                sender -> dispatch(sender, notification, devices),
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

    private boolean cantSendByPreference(Notification notification) {
        NotificationPreference pref = notificationPreferenceRepository.findByMemberId(notification.getReceiverId())
            .orElseThrow(() -> new ReceiverNotificationPreferenceNotFoundException(notification.getReceiverId()));
        return !pref.canReceive(notification.getType());
    }

    private List<DeviceRegistration> fetchActiveDevices(long receiverId) {
        return deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(receiverId);
    }

    private void dispatch(NotificationSender sender, Notification notification, List<DeviceRegistration> devices) {
        try {
            sender.send(notification, devices);
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
