package atwoz.atwoz.block.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(
    name = "blocks",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_blocker_id_blocked_id", columnNames = {"blockerId", "blockedId"})
    }
)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Block extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blockerId;

    private Long blockedId;

    private Block(@NonNull Long blockerId, @NonNull Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("blockerId와 blockedId는 같을 수 없습니다.");
        }
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    public static Block of(Long blockerId, Long blockedId) {
        return new Block(blockerId, blockedId);
    }
}
