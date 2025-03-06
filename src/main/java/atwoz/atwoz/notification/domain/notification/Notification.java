package atwoz.atwoz.notification.domain.notification;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private SenderType senderType;

    private Long receiverId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private NotificationType type;

    private String title;

    private String content;

    private Boolean isRead;

    @Builder
    private Notification(Long senderId, SenderType senderType, Long receiverId, NotificationType type, String title, String content) {
        setSenderId(senderId);
        setSenderType(senderType);
        setReceiverId(receiverId);
        setType(type);
        setTitle(title);
        setContent(content);
        this.isRead = false;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setSenderType(@NonNull SenderType senderType) {
        this.senderType = senderType;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setType(@NonNull NotificationType type) {
        this.type = type;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }
}
