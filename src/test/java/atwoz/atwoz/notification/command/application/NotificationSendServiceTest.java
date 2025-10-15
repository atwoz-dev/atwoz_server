package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static atwoz.atwoz.notification.command.domain.ChannelType.PUSH;
import static atwoz.atwoz.notification.command.domain.NotificationStatus.*;
import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.SenderType.SYSTEM;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSendServiceTest {

    @Mock
    NotificationCommandRepository notificationCommandRepository;

    @Mock
    NotificationPreferenceCommandRepository notificationPreferenceCommandRepository;

    @Mock
    NotificationTemplateCommandRepository notificationTemplateCommandRepository;

    @Mock
    DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;

    @Mock
    NotificationSenderResolver notificationSenderResolver;

    @InjectMocks
    NotificationSendService service;

    @Test
    @DisplayName("send(): 템플릿 없으면 FAILED_TEMPLATE_NOT_FOUND 상태로 저장")
    void sendSavesFailedTemplate() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 1L, 2L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.empty());

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == FAILED_TEMPLATE_NOT_FOUND));
    }

    @Test
    @DisplayName("send(): 수신 설정 없으면 FAILED_PREFERENCE_NOT_FOUND 상태로 저장")
    void sendSavesFailedPreference() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 1L, 99L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "", "")));
        when(notificationPreferenceCommandRepository.findByMemberId(99L))
            .thenReturn(Optional.empty());

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == FAILED_PREFERENCE_NOT_FOUND));
    }

    @Test
    @DisplayName("send(): 수신 거부 시 REJECTED_BY_PREFERENCE 상태로 저장")
    void sendSavesRejectedNotification() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 1L, 1L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "", "")));

        var pref = NotificationPreference.of(1L);
        pref.disableGlobally();
        when(notificationPreferenceCommandRepository.findByMemberId(1L))
            .thenReturn(Optional.of(pref));

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == REJECTED_BY_PREFERENCE));
    }

    @Test
    @DisplayName("send(): 정상 전송 시 SENT 상태로 저장")
    void sendSavesSentNotification() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 10L, 20L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "t", "b")));
        when(notificationPreferenceCommandRepository.findByMemberId(20L))
            .thenReturn(Optional.of(NotificationPreference.of(20L)));
        when(deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(20L))
            .thenReturn(Optional.of(DeviceRegistration.of(20L, "device", "token")));

        var sender = mock(NotificationSender.class);
        when(notificationSenderResolver.resolve(PUSH))
            .thenReturn(Optional.of(sender));

        // when
        service.send(req);

        // then
        verify(sender).send(any(Notification.class), any(DeviceRegistration.class));
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == SENT));
    }

    @Test
    @DisplayName("send(): 디바이스 없으면 FAILED_DEVICE_NOT_FOUND 상태로 저장")
    void sendSavesFailedDevice() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 1L, 30L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "", "")));
        when(notificationPreferenceCommandRepository.findByMemberId(30L))
            .thenReturn(Optional.of(NotificationPreference.of(30L)));
        when(deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(30L))
            .thenReturn(Optional.empty());

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == FAILED_DEVICE_NOT_FOUND));
    }

    @Test
    @DisplayName("send(): 지원하지 않는 채널 시 FAILED_UNSUPPORTED_CHANNEL 상태로 저장")
    void sendSavesUnsupportedChannel() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 10L, 20L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "t", "b")));
        when(notificationPreferenceCommandRepository.findByMemberId(20L))
            .thenReturn(Optional.of(NotificationPreference.of(20L)));
        when(deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(20L))
            .thenReturn(Optional.of(DeviceRegistration.of(20L, "device", "token")));
        when(notificationSenderResolver.resolve(PUSH))
            .thenReturn(Optional.empty());

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == FAILED_UNSUPPORTED_CHANNEL));
    }

    @Test
    @DisplayName("send(): 전송 중 예외 발생 시 FAILED_EXCEPTION 상태로 저장")
    void sendSavesFailedException() {
        // given
        var req = new NotificationSendRequest(SYSTEM, 10L, 20L, LIKE, Map.of(), PUSH);
        when(notificationTemplateCommandRepository.findByType(LIKE))
            .thenReturn(Optional.of(NotificationTemplate.of(LIKE, "t", "b")));
        when(notificationPreferenceCommandRepository.findByMemberId(20L))
            .thenReturn(Optional.of(NotificationPreference.of(20L)));
        when(deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(20L))
            .thenReturn(Optional.of(DeviceRegistration.of(20L, "device", "token")));

        var sender = mock(NotificationSender.class);
        when(notificationSenderResolver.resolve(PUSH))
            .thenReturn(Optional.of(sender));
        doThrow(new NotificationSendFailedException(new RuntimeException("FCM 전송 실패")))
            .when(sender).send(any(Notification.class), any(DeviceRegistration.class));

        // when
        service.send(req);

        // then
        verify(notificationCommandRepository)
            .save(argThat(n -> n.getStatus() == FAILED_EXCEPTION));
    }
}
