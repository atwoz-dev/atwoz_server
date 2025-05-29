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

import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.SenderType.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationReadServiceTest {

    @Mock
    NotificationCommandRepository repository;

    @InjectMocks
    NotificationReadService service;

    @Test
    @DisplayName("markAsRead(): 존재하는 알림의 읽음 상태가 true로 변경")
    void markAsReadUpdatesReadState() {
        // given
        long notificationId = 1L;
        var notification = Notification.create(SYSTEM, 10L, 20L, LIKE, "t", "b");
        when(repository.findById(notificationId)).thenReturn(Optional.of(notification));

        // when
        service.markAsRead(notificationId);

        // then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    @DisplayName("markAsRead(): 알림이 없으면 NotificationNotFoundException 발생")
    void markAsReadThrowsWhenNotFound() {
        // given
        long notificationId = 2L;
        when(repository.findById(notificationId)).thenReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> service.markAsRead(notificationId))
            .isInstanceOf(NotificationNotFoundException.class);
    }
}
