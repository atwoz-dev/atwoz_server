package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.NotificationSender;
import atwoz.atwoz.notification.command.domain.notification.message.MessageGenerator;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateFactory;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSettingCommandRepository;
import atwoz.atwoz.notification.command.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.notification.command.application.notification.NotificationMapper.toNotification;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberCommandRepository memberCommandRepository;
    private final NotificationCommandRepository notificationCommandRepository;
    private final NotificationSettingCommandRepository notificationSettingCommandRepository;

    private final MessageTemplateFactory messageTemplateFactory;
    private final MessageGenerator messageGenerator;
    private final NotificationSender notificationSender;

    @Transactional
    public void send(NotificationRequest request) {
        Notification notification = createNotification(request);
        notificationCommandRepository.save(notification);
        sendIfOptedIn(request.receiverId(), notification);
    }

    private Notification createNotification(NotificationRequest request) {
        Notification notification = toNotification(request);

        String receiverName = null;
        if (notification.isSocialType()) {
            receiverName = getReceiverName(request.receiverId());
        }

        notification.setMessage(messageTemplateFactory, messageGenerator, receiverName);
        return notification;
    }

    private String getReceiverName(long receiverId) {
        return memberCommandRepository.findById(receiverId)
                .orElseThrow(MemberNotFoundException::new)
                .getProfile()
                .getNickname()
                .getValue();
    }

    private void sendIfOptedIn(long receiverId, Notification notification) {
        NotificationSetting receiverSetting = getNotificationSetting(receiverId);
        if (receiverSetting.isOptedIn()) {
            notificationSender.send(notification, receiverSetting.getDeviceToken());
        }
    }

    private NotificationSetting getNotificationSetting(long receiverId) {
        return notificationSettingCommandRepository.findByMemberId(receiverId)
                .orElseThrow(() -> new NotificationReceiverNotFoundException(receiverId));
    }
}
