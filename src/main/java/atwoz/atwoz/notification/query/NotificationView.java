package atwoz.atwoz.notification.query;

import atwoz.atwoz.notification.command.domain.NotificationType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationView(
    long notificationId,
    long senderId,
    @Schema(implementation = NotificationType.class)
    String notificationType,
    String title,
    String body
) {
    @QueryProjection
    public NotificationView {
    }
}
