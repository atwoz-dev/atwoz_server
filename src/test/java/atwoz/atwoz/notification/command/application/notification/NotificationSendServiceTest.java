package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.notification.command.application.NotificationSendRequest;
import atwoz.atwoz.notification.command.application.NotificationSendService;
import atwoz.atwoz.notification.command.application.ReceiverNotificationSettingNotFoundException;
import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSendService 테스트")
class NotificationSendServiceTest {

    @Mock
    private NotificationCommandRepository notificationCommandRepository;

    @Mock
    private NotificationPreferenceCommandRepository notificationPreferenceCommandRepository;

    @Mock
    private NotificationSendDomainService notificationSendDomainService;

    @InjectMocks
    private NotificationSendService notificationSendService;

    @Test
    @DisplayName("정상 요청 시, 수신자의 NotificationSetting을 찾고 도메인 서비스를 호출한 뒤 저장한다.")
    void sendNotificationSuccessfully() {
        // given
        long receiverId = 2L;
        NotificationSendRequest request = new NotificationSendRequest(1L, "MEMBER", receiverId, "MATCH_REQUESTED");
        NotificationPreference setting = mock(NotificationPreference.class);

        when(notificationPreferenceCommandRepository.findByMemberId(receiverId)).thenReturn(Optional.of(setting));

        // when
        notificationSendService.send(request);

        // then
        verify(notificationSendDomainService).send(any(Notification.class), eq(setting));
        verify(notificationCommandRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("수신자의 NotificationSetting이 없으면 ReceiverNotificationSettingNotFoundException 예외를 발생시킨다.")
    void sendNotificationThrowsWhenNoSetting() {
        // given
        long receiverId = 2L;
        NotificationSendRequest request = new NotificationSendRequest(1L, "MEMBER", receiverId, "MATCH_REQUESTED");

        when(notificationPreferenceCommandRepository.findByMemberId(receiverId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationSendService.send(request))
            .isInstanceOf(ReceiverNotificationSettingNotFoundException.class);
    }
}
