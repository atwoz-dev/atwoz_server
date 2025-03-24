package atwoz.atwoz.like.command.domain.like;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.like.command.domain.like.event.LikeMakedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"senderId", "receiverId"}),
        indexes = {
                @Index(name = "idx_receiver_id", columnList = "receiverId")
        })
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
    private LikeLevel likeLevel;

    public static Like of(Long senderId, Long receiverId, LikeLevel likeLevel) {
        Events.raise(LikeMakedEvent.of(senderId, receiverId)); // 좋아요 알림을 위한 이벤트.
        return new Like(senderId, receiverId, likeLevel);
    }

    private Like(@NonNull Long senderId, @NonNull Long receiverId, @NonNull LikeLevel likeLevel) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.likeLevel = likeLevel;
    }
}
