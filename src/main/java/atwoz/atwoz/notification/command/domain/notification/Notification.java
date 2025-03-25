package atwoz.atwoz.notification.command.domain.notification;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.notification.command.domain.notification.message.MessageGenerator;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import jakarta.persistence.*;
import lombok.*;

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
    @Getter
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

    public void setMessage(MessageTemplate template, MessageGenerator generator) {
        setTitle(generator.createTitle(template));
        setContent(generator.createContent(template));
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
