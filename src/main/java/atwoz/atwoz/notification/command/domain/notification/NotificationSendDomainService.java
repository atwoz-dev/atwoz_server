package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.message.MessageGenerator;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateFactory;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSendDomainService {

    private final MemberCommandRepository memberCommandRepository;
    private final MessageTemplateFactory messageTemplateFactory;
    private final MessageGenerator messageGenerator;
    private final NotificationSender notificationSender;

    public void send(Notification notification, NotificationSetting receiverNotificationSetting) {
        setMessage(notification);
        sendIfOptedIn(notification, receiverNotificationSetting);
    }

    private void setMessage(Notification notification) {
        String receiverName = null;
        if (notification.isSocialType()) {
            receiverName = getReceiverName(notification.getReceiverId());
        }
        notification.setMessage(messageTemplateFactory, messageGenerator, receiverName);
    }

    private String getReceiverName(long receiverId) {
        return memberCommandRepository.findById(receiverId)
                .orElseThrow(MemberNotFoundException::new)
                .getProfile()
                .getNickname()
                .getValue();
    }

    private void sendIfOptedIn(Notification notification, NotificationSetting receiverNotificationSetting) {
        if (receiverNotificationSetting.isOptedIn()) {
            notificationSender.send(notification, receiverNotificationSetting.getDeviceToken());
        }
    }
}
