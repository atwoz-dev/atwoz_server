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
        long receiverId = 100L;
        Notification notification1 = createNotification(receiverId, "Title 1", "Content 1", true);
        Notification notification2 = createNotification(receiverId, "Title 2", "Content 2", false);
        Notification notification3 = createNotification(200L, "Title 3", "Content 3", false);

        em.persist(notification1);
        em.persist(notification2);
        em.persist(notification3);
        em.flush();
        em.clear();

        // when
        List<NotificationView> result = notificationQueryRepository.findNotifications(receiverId, true);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().senderId()).isEqualTo(111L);
        assertThat(result.getFirst().title()).isEqualTo("Title 1");
        assertThat(result.getFirst().content()).isEqualTo("Content 1");
    }

    @Test
    @DisplayName("읽지 않은 상태의 알림을 조회한다.")
    void findUnreadNotificationsByReceiverId() throws NoSuchFieldException, IllegalAccessException {
        // given
        long receiverId = 100L;
        Notification notification1 = createNotification(receiverId, "Title 1", "Content 1", false);
        Notification notification2 = createNotification(receiverId, "Title 2", "Content 2", false);
        Notification notification3 = createNotification(200L, "Title 3", "Content 3", false);

        em.persist(notification1);
        em.persist(notification2);
        em.persist(notification3);
        em.flush();
        em.clear();

        // when
        List<NotificationView> result = notificationQueryRepository.findNotifications(receiverId, false);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title").containsExactlyInAnyOrder("Title 1", "Title 2");
    }

    private Notification createNotification(long receiverId, String title, String content, boolean isRead) throws NoSuchFieldException, IllegalAccessException {
        Notification notification = Notification.of(111L, SenderType.MEMBER, receiverId, NotificationType.MATCH_REQUESTED);

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
