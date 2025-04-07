package atwoz.atwoz.notification.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.notification.command.domain.notification.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<NotificationView> findNotifications(long receiverId, boolean isRead) {
        return queryFactory
                .select(new QNotificationView(
                        notification.id,
                        notification.senderId,
                        notification.title,
                        notification.content
                ))
                .from(notification)
                .where(notification.receiverId.eq(receiverId), notification.isRead.eq(isRead))
                .orderBy(notification.createdAt.desc())
                .fetch();
    }
}
