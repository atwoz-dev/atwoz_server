package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Events;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class WarningTest {

    @Test
    @DisplayName("issue() 호출 시 WarningIssuedEvent 발생 및 Warning 객체가 올바르게 반환된다")
    void issueShouldRaiseWarningIssuedEventAndReturnWarning() {
        try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
            // given
            long adminId = 1L;
            long memberId = 2L;
            var reason = WarningReasonType.INAPPROPRIATE_CONTENT;

            // when
            var warning = Warning.issue(adminId, memberId, reason);

            // then
            eventsMock.verify(() -> Events.raise(any(WarningIssuedEvent.class)));
            assertEquals(adminId, warning.getAdminId());
            assertEquals(memberId, warning.getMemberId());
            assertEquals(reason, warning.getReasonType());
        }
    }

    @Test
    @DisplayName("checkIfThresholdExceeded() 호출 시 임계치 미만이면 이벤트가 발생하지 않는다")
    void checkIfThresholdExceededWhenBelowThresholdShouldNotRaiseEvent() {
        try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
            // given
            long adminId = 1L;
            long memberId = 2L;
            var warning = Warning.issue(adminId, memberId, WarningReasonType.INAPPROPRIATE_CONTENT);
            eventsMock.clearInvocations();

            // when
            warning.checkIfThresholdExceeded(1);

            // then
            eventsMock.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("checkIfThresholdExceeded() 호출 시 임계치 도달 또는 초과하면 WarningThresholdExceededEvent가 발생한다")
    void checkIfThresholdExceededWhenAtOrAboveThresholdShouldRaiseThresholdExceededEvent() {
        try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
            // given
            long adminId = 1L;
            long memberId = 2L;
            var warning = Warning.issue(adminId, memberId, WarningReasonType.INAPPROPRIATE_CONTENT);
            eventsMock.clearInvocations();

            // when
            warning.checkIfThresholdExceeded(2);

            // then
            eventsMock.verify(() -> Events.raise(any(WarningThresholdExceededEvent.class)));
        }
    }
}
