package atwoz.atwoz.mission.command.domain.mission;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private boolean isPublic;

    private int rewardedHearts;

    @Builder
    private Mission(ActionType actionType, FrequencyType frequencyType, int requiredAttempt, int repeatableCount,
        boolean isPublic, int rewardedHearts) {
        this.actionType = actionType;
        this.frequencyType = frequencyType;
        this.requiredAttempt = requiredAttempt;
        this.repeatableCount = repeatableCount;
        this.isPublic = isPublic;
        this.rewardedHearts = rewardedHearts;
    }
}
