package atwoz.atwoz.notification.query;

import atwoz.atwoz.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, NotificationQueryRepository.class})
class NotificationQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private NotificationQueryRepository notificationQueryRepository;


}
