package atwoz.atwoz.mission.command.domain.mission;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.mission.command.domain.mission.exception.MustBePositiveException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private FrequencyType frequencyType;

    private int requiredAttempt;

    private int repeatableCount;

    private int rewardedHeart;

    private boolean isPublic;

    @Builder
    private Mission(@NonNull ActionType actionType, @NonNull FrequencyType frequencyType, int requiredAttempt,
        int repeatableCount,
        int rewardedHeart, boolean isPublic) {
        validateRequiredAttempt(requiredAttempt);
        validateRepeatableCount(repeatableCount);
        validateRewardedHearts(rewardedHeart);
        this.actionType = actionType;
        this.frequencyType = frequencyType;
        this.requiredAttempt = requiredAttempt;
        this.repeatableCount = repeatableCount;
        this.rewardedHeart = rewardedHeart;
        this.isPublic = isPublic;
    }

    private void validateRequiredAttempt(int requiredAttempt) {
        if (requiredAttempt <= 0) {
            throw new MustBePositiveException(requiredAttempt);
        }
    }

    private void validateRepeatableCount(int repeatableCount) {
        if (repeatableCount <= 0) {
            throw new MustBePositiveException(repeatableCount);
        }
    }

    private void validateRewardedHearts(int rewardedHeart) {
        if (rewardedHeart <= 0) {
            throw new MustBePositiveException(rewardedHeart);
        }
    }
}
