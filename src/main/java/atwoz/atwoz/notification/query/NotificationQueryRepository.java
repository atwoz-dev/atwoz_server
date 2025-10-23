package atwoz.atwoz.notification.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.notification.command.domain.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private static final int PAGE_SIZE = 20;
    
    private final JPAQueryFactory queryFactory;

    public List<NotificationView> findNotifications(long receiverId, Long lastId) {
        BooleanExpression condition = notification.receiverId.eq(receiverId)
            .and(notification.deletedAt.isNull());

        if (lastId != null) {
            condition = condition.and(notification.id.lt(lastId));
        }

        return queryFactory
            .select(new QNotificationView(
                notification.id,
                notification.senderId,
                notification.receiverId,
                notification.type.stringValue(),
                notification.title,
                notification.body,
                notification.readAt.isNotNull(),
                notification.createdAt
            ))
            .from(notification)
            .where(condition)
            .orderBy(notification.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }
}
