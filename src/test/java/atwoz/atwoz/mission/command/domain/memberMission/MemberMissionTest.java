package atwoz.atwoz.mission.command.domain.memberMission;

import atwoz.atwoz.mission.command.domain.memberMission.exception.MustNotBeNegativeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class MemberMissionTest {
    @Nested
    @DisplayName("멤버 미션 생성 테스트")
    class Create {

        private static Stream<Arguments> constructorSource() {
            return Stream.of(Arguments.of("시도횟수가 음수인 경우 예외 발생", 1L, 2L, -1, 1, false),
                Arguments.of("완료횟수가 음수인 경우 예외 발생", 1L, 2L, 0, -1, false));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("constructorSource")
        void throwExceptionWhenIntValueIsLessThan0(String name, long memberId, long missionId, int attemptCount,
            int successCount, boolean isCompleted) {
            // When & Then
            Assertions.assertThatThrownBy(() -> MemberMission.builder()
                .memberId(memberId)
                .missionId(missionId)
                .attemptCount(attemptCount)
                .successCount(successCount)
                .isCompleted(isCompleted)
                .build()).isInstanceOf(MustNotBeNegativeException.class);
        }

        @DisplayName("멤버 미션을 생성합니다.")
        @Test
        void createMemberMission() {
            // Given
            long memberId = 1L;
            long missionId = 2L;
            int attemptCount = 3;
            int successCount = 0;
            boolean isCompleted = false;

            // When
            MemberMission memberMission = MemberMission.builder()
                .memberId(memberId)
                .missionId(missionId)
                .attemptCount(attemptCount)
                .successCount(successCount)
                .isCompleted(isCompleted)
                .build();

            // Then
            Assertions.assertThat(memberMission.getMemberId()).isEqualTo(memberId);
            Assertions.assertThat(memberMission.getMissionId()).isEqualTo(missionId);
            Assertions.assertThat(memberMission.getAttemptCount()).isEqualTo(attemptCount);
            Assertions.assertThat(memberMission.getSuccessCount()).isEqualTo(successCount);
            Assertions.assertThat(memberMission.isCompleted()).isEqualTo(isCompleted);
        }
    }
}
