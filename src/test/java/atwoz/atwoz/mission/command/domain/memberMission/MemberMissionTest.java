package atwoz.atwoz.mission.command.domain.memberMission;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberMissionTest {
    @Nested
    @DisplayName("멤버 미션 생성 테스트")
    class Create {

        @DisplayName("멤버 미션을 생성합니다.")
        @Test
        void createMemberMission() {
            // Given
            long memberId = 1L;
            long missionId = 2L;
            int attemptCount = 0;
            int successCount = 0;
            boolean isCompleted = false;

            // When
            MemberMission memberMission = MemberMission.create(memberId, missionId);

            // Then
            Assertions.assertThat(memberMission.getMemberId()).isEqualTo(memberId);
            Assertions.assertThat(memberMission.getMissionId()).isEqualTo(missionId);
            Assertions.assertThat(memberMission.getAttemptCount()).isEqualTo(attemptCount);
            Assertions.assertThat(memberMission.getSuccessCount()).isEqualTo(successCount);
            Assertions.assertThat(memberMission.isCompleted()).isEqualTo(isCompleted);
        }
    }
}
