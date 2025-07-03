package atwoz.atwoz.mission.command.domain.memberMission;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.mission.command.domain.memberMission.exception.MustNotBeNegativeException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long memberId;

    private long missionId;

    private int attemptCount;

    private int successCount;

    private boolean isCompleted;

    @Builder
    private MemberMission(long memberId, long missionId, int attemptCount, int successCount, boolean isCompleted) {
        validateAttemptCount(attemptCount);
        validateSuccessCount(successCount);
        this.memberId = memberId;
        this.missionId = missionId;
        this.attemptCount = attemptCount;
        this.successCount = successCount;
        this.isCompleted = isCompleted;
    }

    private void validateAttemptCount(int attemptCount) {
        if (attemptCount < 0) {
            throw new MustNotBeNegativeException(attemptCount);
        }
    }

    private void validateSuccessCount(int successCount) {
        if (successCount < 0) {
            throw new MustNotBeNegativeException(successCount);
        }
    }
}
