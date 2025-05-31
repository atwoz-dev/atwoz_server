package atwoz.atwoz.admin.command.domain.suspension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuspensionPolicyTest {

    private final SuspensionPolicy policy = new SuspensionPolicy();

    @Test
    @DisplayName("evaluate(): 경고 수가 임계값 미만이면 TEMPORARY 반환")
    void evaluateBelowThresholdReturnsTemporary() {
        // given
        var warningCount = 2L; // 임계값 3 미만

        // when
        var result = policy.evaluate(warningCount);

        // then
        assertThat(result).isEqualTo(SuspensionStatus.TEMPORARY);
    }

    @Test
    @DisplayName("evaluate(): 경고 수가 임계값과 같으면 PERMANENT 반환")
    void evaluateAtThresholdReturnsPermanent() {
        // given
        var warningCount = 3L; // 임계값 3

        // when
        var result = policy.evaluate(warningCount);

        // then
        assertThat(result).isEqualTo(SuspensionStatus.PERMANENT);
    }

    @Test
    @DisplayName("evaluate(): 경고 수가 임계값 초과이면 PERMANENT 반환")
    void evaluateAboveThresholdReturnsPermanent() {
        // given
        var warningCount = 5L; // 임계값 3 초과

        // when
        var result = policy.evaluate(warningCount);

        // then
        assertThat(result).isEqualTo(SuspensionStatus.PERMANENT);
    }
}
