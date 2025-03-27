package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.notification.command.domain.notification.message.*;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static atwoz.atwoz.notification.command.domain.notification.NotificationType.INAPPROPRIATE_CONTENT;
import static atwoz.atwoz.notification.command.domain.notification.NotificationType.MATCH_REQUESTED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSendDomainService 테스트")
class NotificationSendDomainServiceTest {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private MessageTemplateFactory messageTemplateFactory;

    @Mock
    private MessageGenerator messageGenerator;

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private NotificationSendDomainService notificationSendDomainService;

    @Test
    @DisplayName("소셜 알림이고 opt-in되어 있으면, 메시지 설정 후 푸시를 전송한다.")
    void sendSocialNotificationOptIn() {
        // given
        long receiverId = 2L;
        String receiverDeviceToken = "receiverDeviceToken";
        String receiverName = "홍길동";

        Notification notification = createNotification(MATCH_REQUESTED, receiverId);
        NotificationSetting setting = createNotificationSetting(receiverId, true, receiverDeviceToken);

        MessageTemplate template = MatchRequestedMessageTemplate.from(receiverName);
        when(messageTemplateFactory.create(any())).thenReturn(template);
        when(messageGenerator.createTitle(any())).thenReturn("title");

        Member member = mock(Member.class);
        when(memberCommandRepository.findById(notification.getReceiverId())).thenReturn(Optional.of(member));

        MemberProfile profile = mock(MemberProfile.class);
        when(member.getProfile()).thenReturn(profile);

        Nickname nickname = mock(Nickname.class);
        when(profile.getNickname()).thenReturn(nickname);
        when(nickname.getValue()).thenReturn(receiverName);

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender).send(notification, receiverDeviceToken);
    }

    @Test
    @DisplayName("소셜 알림이지만 opt-out되어 있으면, 푸시를 전송하지 않는다.")
    void sendSocialNotificationOptOut() {
        // given
        long receiverId = 2L;
        String receiverDeviceToken = "test_token_123";
        String receiverName = "홍길동";

        Notification notification = createNotification(MATCH_REQUESTED, receiverId);
        NotificationSetting setting = createNotificationSetting(receiverId, false, receiverDeviceToken);

        MessageTemplate template = MatchRequestedMessageTemplate.from(receiverName);
        when(messageTemplateFactory.create(any())).thenReturn(template);
        when(messageGenerator.createTitle(any())).thenReturn("notification title");

        Member member = mock(Member.class);
        when(memberCommandRepository.findById(notification.getReceiverId())).thenReturn(Optional.of(member));

        MemberProfile profile = mock(MemberProfile.class);
        when(member.getProfile()).thenReturn(profile);

        Nickname nickname = mock(Nickname.class);
        when(profile.getNickname()).thenReturn(nickname);
        when(nickname.getValue()).thenReturn(receiverName);

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender, never()).send(any(), any());
    }

    @Test
    @DisplayName("수신자 정보를 찾을 수 없다면 MemberNotFoundException이 발생한다.")
    void sendSocialNotificationMemberNotFound() {
        // given
        long receiverId = 2L;
        Notification notification = createNotification(MATCH_REQUESTED, receiverId);
        NotificationSetting setting = createNotificationSetting(receiverId, true, "test_token_123");

        when(memberCommandRepository.findById(notification.getReceiverId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationSendDomainService.send(notification, setting))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("소셜 알림이 아닌 경우, 수신자 닉네임 조회 없이 메시지를 설정하며 opt-in시 푸시를 전송한다.")
    void sendNonSocialNotificationOptIn() {
        // given
        long receiverId = 2L;
        Notification notification = createNotification(INAPPROPRIATE_CONTENT, receiverId);
        String receiverDeviceToken = "test_token_123";
        NotificationSetting setting = createNotificationSetting(receiverId, true, receiverDeviceToken);

        MessageTemplate template = new DefaultMessageTemplate();
        when(messageTemplateFactory.create(any())).thenReturn(template);
        when(messageGenerator.createTitle(any())).thenReturn("notification title");

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender).send(notification, receiverDeviceToken);
        verify(memberCommandRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("소셜 알림이 아니고, opt-out이면 푸시를 전송하지 않는다.")
    void sendNonSocialNotificationOptOut() {
        // given
        long receiverId = 2L;
        Notification notification = createNotification(INAPPROPRIATE_CONTENT, receiverId);
        NotificationSetting setting = createNotificationSetting(receiverId, false, "test_token_123");

        MessageTemplate template = new DefaultMessageTemplate();
        when(messageTemplateFactory.create(any())).thenReturn(template);
        when(messageGenerator.createTitle(any())).thenReturn("notification title");

        // when
        notificationSendDomainService.send(notification, setting);

        // then
        verify(notificationSender, never()).send(any(), any());
        verify(memberCommandRepository, never()).findById(anyLong());
    }

    private Notification createNotification(NotificationType type, long receiverId) {
        return Notification.of(1L, SenderType.MEMBER, receiverId, type);
    }

    private NotificationSetting createNotificationSetting(long receiverId, boolean optedIn, String deviceToken) {
        NotificationSetting notificationSetting = NotificationSetting.of(receiverId);
        if (optedIn) {
            notificationSetting.optIn();
        }
        notificationSetting.updateDeviceToken(deviceToken);
        return notificationSetting;
    }
}
