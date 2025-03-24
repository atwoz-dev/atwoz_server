package atwoz.atwoz.like.command.domain.like;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "likes", indexes = {
        @Index(name = "idx_sender_id", columnList = "senderId"),
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

    private LikeLevel likeLevel;

    public static Like of(Long senderId, Long receiverId, LikeLevel likeLevel) {
        return new Like(senderId, receiverId, likeLevel);
    }

    private Like(@NonNull Long senderId, @NonNull Long receiverId, @NonNull LikeLevel likeLevel) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.likeLevel = likeLevel;
    }
}
