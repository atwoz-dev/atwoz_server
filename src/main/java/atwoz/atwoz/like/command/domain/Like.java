package atwoz.atwoz.like.command.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"senderId", "receiverId"}),
    indexes = {@Index(name = "idx_receiver_id", columnList = "receiverId")}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private LikeLevel level;

    private Like(long senderId, long receiverId, @NonNull LikeLevel level) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.level = level;
    }

    public static Like of(long senderId, long receiverId, LikeLevel level) {
        Events.raise(LikeSentEvent.of(senderId, receiverId));
        return new Like(senderId, receiverId, level);
    }
}
