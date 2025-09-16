package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.SenderType.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
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
        when(repository.findAllById(List.of(notificationId))).thenReturn(List.of(notification));
        var request = new NotificationReadRequest(List.of(notificationId));

        // when
        service.markAsRead(request);

        // then
        assertThat(notification.isRead()).isTrue();
    }


    @Test
    @DisplayName("markAsRead(): 여러 알림의 읽음 상태가 모두 true로 변경")
    void markAsReadUpdatesMultipleNotifications() {
        // given
        long notificationId1 = 1L;
        long notificationId2 = 2L;
        var notification1 = Notification.create(SYSTEM, 10L, 20L, LIKE, "t1", "b1");
        var notification2 = Notification.create(SYSTEM, 10L, 20L, LIKE, "t2", "b2");
        when(repository.findAllById(List.of(notificationId1, notificationId2))).thenReturn(
            List.of(notification1, notification2));
        var request = new NotificationReadRequest(List.of(notificationId1, notificationId2));

        // when
        service.markAsRead(request);

        // then
        assertThat(notification1.isRead()).isTrue();
        assertThat(notification2.isRead()).isTrue();
    }
}
