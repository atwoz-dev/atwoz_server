package atwoz.atwoz.notification.command.domain;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private SenderType senderType;

    private Long senderId;

    private Long receiverId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private NotificationType type;

    private String title;

    private String body;

    private LocalDateTime readAt = null;

    private Notification(
        SenderType senderType,
        Long senderId,
        Long receiverId,
        NotificationType type,
        String title,
        String body
    ) {
        this.senderType = senderType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public static Notification create(
        @NonNull SenderType senderType,
        long senderId,
        long receiverId,
        @NonNull NotificationType type,
        String title,
        String body
    ) {
        return new Notification(senderType, senderId, receiverId, type, title, body);
    }

    public boolean isRead() {
        return readAt != null;
    }

    public void markAsRead() {
        if (this.readAt != null) {
            return;
        }
        readAt = LocalDateTime.now();
    }
}
