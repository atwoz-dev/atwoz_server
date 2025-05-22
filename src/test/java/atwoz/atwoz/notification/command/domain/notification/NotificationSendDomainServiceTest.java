package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.notification.command.domain.*;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSendDomainService 테스트")
class NotificationSendDomainServiceTest {

    @Mock
    private MessageTemplateFactory messageTemplateFactory;

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private NotificationSendDomainService notificationSendDomainService;

    @Test
    @DisplayName("수신자 설정이 opt-in되어 있으면, 메시지 설정 후 푸시를 전송한다.")
    void sendSocialNotificationOptIn() {
        // given
        String receiverDeviceToken = "receiverDeviceToken";
        Notification notification = createNotification();
        NotificationPreference setting = createNotificationSetting(true, receiverDeviceToken);

        MessageTemplate template = mock(MessageTemplate.class);
        when(template.getTitle(any())).thenReturn("title");
        when(template.getContent(any())).thenReturn("content");

        when(messageTemplateFactory.getByNotificationType(any(NotificationType.class))).thenReturn(template);

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender).send(notification, receiverDeviceToken);
    }

    @Test
    @DisplayName("수신자 설정이 opt-out되어 있으면, 푸시를 전송하지 않는다.")
    void sendSocialNotificationOptOut() {
        // given
        String receiverDeviceToken = "receiverDeviceToken";
        Notification notification = createNotification();
        NotificationPreference setting = createNotificationSetting(false, receiverDeviceToken);

        MessageTemplate template = mock(MessageTemplate.class);
        when(template.getTitle(any())).thenReturn("title");
        when(template.getContent(any())).thenReturn("content");

        when(messageTemplateFactory.getByNotificationType(any(NotificationType.class))).thenReturn(template);

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender, never()).send(any(), any());
    }

    private Notification createNotification() {
        return Notification.create(1L, SenderType.MEMBER, 2L, NotificationType.MATCH_REQUEST);
    }

    private NotificationPreference createNotificationSetting(boolean optedIn, String deviceToken) {
        NotificationPreference notificationPreference = NotificationPreference.of(2L);
        if (optedIn) {
            notificationPreference.optIn();
        }
        notificationPreference.updateDeviceToken(deviceToken);
        return notificationPreference;
    }
}
