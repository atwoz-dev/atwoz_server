package atwoz.atwoz.notification.query;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.notification.command.domain.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static atwoz.atwoz.notification.command.domain.NotificationType.LIKE;
import static atwoz.atwoz.notification.command.domain.SenderType.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, NotificationQueryRepository.class})
class NotificationQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private NotificationQueryRepository repository;

    @Test
    @DisplayName("findNotifications(): 읽지 않은 알림만 조회")
    void findUnreadNotifications() {
        // given
        var unread = Notification.create(SYSTEM, 1L, 2L, LIKE, "t1", "b1");
        var read = Notification.create(SYSTEM, 1L, 2L, LIKE, "t2", "b2");
        read.markAsRead();
        em.persist(unread);
        em.persist(read);
        em.flush();
        em.clear();

        // when
        var results = repository.findNotifications(2L, false);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("t1");
    }

    @Test
    @DisplayName("findNotifications(): 읽은 알림만 조회")
    void findReadNotifications() {
        // given
        var unread = Notification.create(SYSTEM, 1L, 3L, LIKE, "t3", "b3");
        var read1 = Notification.create(SYSTEM, 1L, 3L, LIKE, "t4", "b4");
        var read2 = Notification.create(SYSTEM, 1L, 3L, LIKE, "t5", "b5");
        read1.markAsRead();
        read2.markAsRead();
        em.persist(unread);
        em.persist(read1);
        em.persist(read2);
        em.flush();
        em.clear();

        // when
        var results = repository.findNotifications(3L, true);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("title")
            .containsExactlyInAnyOrder("t4", "t5");
    }
}
