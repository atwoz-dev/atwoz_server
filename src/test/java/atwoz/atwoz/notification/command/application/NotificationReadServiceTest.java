package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationReadService 테스트")
class NotificationReadServiceTest {

    @Mock
    private NotificationCommandRepository notificationCommandRepository;

    @InjectMocks
    private NotificationReadService notificationReadService;

    @Test
    @DisplayName("정상 요청 시, 알림을 조회하고 markAsRead()를 호출한다.")
    void markAsReadSuccessfully() {
        // given
        long notificationId = 1L;
        Notification notification = mock(Notification.class);
        when(notificationCommandRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // when
        notificationReadService.markAsRead(notificationId);

        // then
        verify(notification).markAsRead();
    }

    @Test
    @DisplayName("존재하지 않는 알림 id 요청 시, NotificationNotFoundException 예외가 발생한다.")
    void markAsReadThrowsWhenNotificationNotFound() {
        // given
        long notificationId = 2L;
        when(notificationCommandRepository.findById(notificationId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationReadService.markAsRead(notificationId))
            .isInstanceOf(NotificationNotFoundException.class);
    }
}
