package atwoz.atwoz.notification.query;

import com.querydsl.core.annotations.QueryProjection;

public record NotificationView(
        long notificationId,
        long senderId,
        String title,
        String content
) {
    @QueryProjection
    public NotificationView {
    }
}
