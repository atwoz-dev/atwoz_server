package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_receiver_is_read", columnList = "receiverId, isRead")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    private Notification(Long senderId, SenderType senderType, Long receiverId, NotificationType type) {
        setSenderId(senderId);
        setSenderType(senderType);
        setReceiverId(receiverId);
        setType(type);
        isRead = false;
    }

    public static Notification of(Long senderId, SenderType senderType, Long receiverId, NotificationType type) {
        return new Notification(senderId, senderType, receiverId, type);
    }

    public boolean isSocialType() {
        return type.isSocial();
    }

    public void setMessage(MessageTemplate template, MessageTemplateParameters parameters) {
        setTitle(template.getTitle(parameters));
        setContent(template.getContent(parameters));
    }

    public void markAsRead() {
        isRead = true;
    }

    private void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    private void setSenderType(@NonNull SenderType senderType) {
        this.senderType = senderType;
    }

    private void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    private void setType(@NonNull NotificationType type) {
        this.type = type;
    }

    private void setTitle(@NonNull String title) {
        this.title = title;
    }

    private void setContent(String content) {
        this.content = content;
    }
}
