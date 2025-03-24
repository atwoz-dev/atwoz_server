package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.*;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSettingCommandRepository;
import atwoz.atwoz.notification.command.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberCommandRepository memberCommandRepository;
    private final NotificationSettingCommandRepository notificationSettingCommandRepository;
    private final NotificationCommandRepository notificationCommandRepository;

    private final NotificationMessageGenerator messageGenerator;
    private final NotificationSender notificationSender;

    @Transactional
    public void sendNotification(NotificationRequest request) {
        String receiverName = getReceiverName(request.receiverId());
        NotificationType notificationType = NotificationType.valueOf(request.notificationType().toUpperCase());

        String title = messageGenerator.generateTitle(notificationType, receiverName);
        String content = messageGenerator.generateContent(notificationType);
        Notification notification = NotificationMapper.toNotification(request, title, content);
        notificationCommandRepository.save(notification);

        NotificationSetting receiverSetting = getNotificationSetting(request.receiverId());
        if (receiverSetting.isOptedIn()) {
            notificationSender.send(notification, receiverSetting.getDeviceToken());
        }
    }

    private String getReceiverName(long receiverId) {
        return memberCommandRepository.findById(receiverId)
                .orElseThrow(MemberNotFoundException::new)
                .getProfile()
                .getNickname()
                .getValue();
    }

    private NotificationSetting getNotificationSetting(long receiverId) {
        return notificationSettingCommandRepository.findByMemberId(receiverId)
                .orElseThrow(() -> new NotificationReceiverNotFoundException(receiverId));
    }
}
