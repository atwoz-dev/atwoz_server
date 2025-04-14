package atwoz.atwoz.notification.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.SenderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Field;
import java.util.List;

import static atwoz.atwoz.notification.command.domain.notification.NotificationType.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, NotificationQueryRepository.class})
class NotificationQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private NotificationQueryRepository notificationQueryRepository;

    @Test
    @DisplayName("읽은 상태의 알림을 조회한다.")
    void findReadNotificationsByReceiverId() throws NoSuchFieldException, IllegalAccessException {
        // given
        long senderId = 123L;
        long receiverId = 100L;
        Notification notification1 = createNotification(senderId, receiverId, MATCH_REQUESTED, "Title 1", "Content 1", true);
        Notification notification2 = createNotification(senderId, receiverId, MATCH_REJECTED, "Title 2", "Content 2", false);
        Notification notification3 = createNotification(200L, receiverId, INAPPROPRIATE_CONTENT, "Title 3", "Content 3", false);

        em.persist(notification1);
        em.persist(notification2);
        em.persist(notification3);
        em.flush();
        em.clear();

        // when
        List<NotificationView> result = notificationQueryRepository.findNotifications(receiverId, true);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().senderId()).isEqualTo(senderId);
        assertThat(result.getFirst().notificationType()).isEqualTo("MATCH_REQUESTED");
        assertThat(result.getFirst().title()).isEqualTo("Title 1");
        assertThat(result.getFirst().content()).isEqualTo("Content 1");
    }

    @Test
    @DisplayName("읽지 않은 상태의 알림을 조회한다.")
    void findUnreadNotificationsByReceiverId() throws NoSuchFieldException, IllegalAccessException {
        // given
        long senderId = 123L;
        long receiverId = 100L;
        Notification notification1 = createNotification(senderId, receiverId, MATCH_REQUESTED, "Title 1", "Content 1", false);
        Notification notification2 = createNotification(senderId, receiverId, MATCH_REJECTED, "Title 2", "Content 2", false);
        Notification notification3 = createNotification(senderId, 200L, INAPPROPRIATE_CONTENT, "Title 3", "Content 3", false);

        em.persist(notification1);
        em.persist(notification2);
        em.persist(notification3);
        em.flush();
        em.clear();

        // when
        List<NotificationView> result = notificationQueryRepository.findNotifications(receiverId, false);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("notificationType").containsExactlyInAnyOrder("MATCH_REQUESTED", "MATCH_COMPLETED");
        assertThat(result).extracting("title").containsExactlyInAnyOrder("Title 1", "Title 2");
        assertThat(result).extracting("content").containsExactlyInAnyOrder("Content 1", "Content 2");
    }

    private Notification createNotification(
            long senderId,
            long receiverId,
            NotificationType type,
            String title,
            String content,
            boolean isRead
    ) throws NoSuchFieldException, IllegalAccessException {
        Notification notification = Notification.of(senderId, SenderType.MEMBER, receiverId, type);

        Field titleField = Notification.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(notification, title);

        Field contentField = Notification.class.getDeclaredField("content");
        contentField.setAccessible(true);
        contentField.set(notification, content);

        Field isReadField = Notification.class.getDeclaredField("isRead");
        isReadField.setAccessible(true);
        isReadField.set(notification, isRead);

        return notification;
    }
}
