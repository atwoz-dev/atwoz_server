package atwoz.atwoz.admin.command.domain.suspension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SuspensionTest {

    @Test
    @DisplayName("of(): 유효한 입력 시 adminId, memberId, status로 Suspension 생성")
    void createSuspensionWithValidInputs() {
        // given
        long expectedAdminId = 42L;
        long expectedMemberId = 100L;
        var expectedStatus = SuspensionStatus.TEMPORARY;

        // when
        var suspension = Suspension.of(expectedAdminId, expectedMemberId, expectedStatus);

        // then
        assertThat(suspension).isNotNull();
        assertThat(suspension.getId()).isNull();
        assertThat(suspension.getAdminId()).isEqualTo(expectedAdminId);
        assertThat(suspension.getMemberId()).isEqualTo(expectedMemberId);
        assertThat(suspension.getStatus()).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("of(): null 상태 입력 시 NullPointerException 발생")
    void ofNullStatusShouldThrowNullPointerException() {
        // given
        long adminId = 1L;
        long memberId = 2L;

        // when / then
        assertThatThrownBy(() -> Suspension.of(adminId, memberId, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("updateStatus(): adminId와 status가 업데이트되어야 함")
    void updateStatusShouldUpdateAdminIdAndStatus() {
        // given
        var originalSuspension = Suspension.of(1L, 2L, SuspensionStatus.TEMPORARY);
        long newAdminId = 99L;
        var newStatus = SuspensionStatus.PERMANENT;

        // when
        originalSuspension.updateStatus(newAdminId, newStatus);

        // then
        assertThat(originalSuspension.getAdminId()).isEqualTo(newAdminId);
        assertThat(originalSuspension.getMemberId()).isEqualTo(2L);
        assertThat(originalSuspension.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("updateStatus(): null 상태 입력 시 NullPointerException 발생")
    void updateStatusNullStatusShouldThrowNullPointerException() {
        // given
        var suspension = Suspension.of(1L, 2L, SuspensionStatus.TEMPORARY);

        // when / then
        assertThatThrownBy(() -> suspension.updateStatus(5L, null))
            .isInstanceOf(NullPointerException.class);
    }
}
