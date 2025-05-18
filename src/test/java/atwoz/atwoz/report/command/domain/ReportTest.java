package atwoz.atwoz.report.command.domain;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.report.command.domain.event.ReportApprovedEvent;
import atwoz.atwoz.report.command.domain.event.ReportCreatedEvent;
import atwoz.atwoz.report.command.domain.exception.InvalidReportResultException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class ReportTest {

    @Nested
    @DisplayName("of 메서드 테스트")
    class OfTest {
        @ParameterizedTest
        @ValueSource(strings = {"reporterId", "reporteeId", "reason", "content"})
        @DisplayName("of 메서드의 필드 값이 null이면 예외를 던진다.")
        void throwsExceptionWhenFieldValueIsNull(String fieldName) {
            // given
            Long reporterId = fieldName.equals("reporterId") ? null : 1L;
            Long reporteeId = fieldName.equals("reporteeId") ? null : 2L;
            ReportReasonType reason = fieldName.equals("reason") ? null : ReportReasonType.ETC;
            String content = fieldName.equals("content") ? null : "content";

            // when, then
            assertThatThrownBy(() -> Report.of(reporterId, reporteeId, reason, content))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("reporterId와 reporteeId가 같으면 예외를 던진다.")
        void throwsExceptionWhenReporterIdAndReporteeIdAreEqual() {
            // given
            Long reporterId = 1L;
            Long reporteeId = 1L;
            ReportReasonType reason = ReportReasonType.ETC;
            String content = "content";

            // when, then
            assertThatThrownBy(() -> Report.of(reporterId, reporteeId, reason, content)).isInstanceOf(
                InvalidReportException.class);
        }

        @Test
        @DisplayName("of 메서드의 필드 값이 정상적이면 객체를 생성하고 이벤트를 발행한다.")
        void createsObjectAndRaiseEventWhenFieldValueIsValid() {
            // given
            Long reporterId = 1L;
            Long reporteeId = 2L;
            ReportReasonType reason = ReportReasonType.ETC;
            String content = "content";

            // when
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                // when
                Report report = Report.of(reporterId, reporteeId, reason, content);

                // then
                eventsMockedStatic.verify(() -> Events.raise(argThat(
                    event -> event instanceof ReportCreatedEvent
                        && ((ReportCreatedEvent) event).getReporterId() == reporterId
                        && ((ReportCreatedEvent) event).getReporteeId() == reporteeId
                )), times(1));

                assertThat(report).isNotNull();
                assertThat(report.getReporterId()).isEqualTo(reporterId);
                assertThat(report.getReporteeId()).isEqualTo(reporteeId);
                assertThat(report.getReason()).isEqualTo(reason);
                assertThat(report.getContent()).isEqualTo(content);
                assertThat(report.getResult()).isEqualTo(ReportResult.PENDING);
            }
        }
    }

    @Nested
    @DisplayName("reject 메서드 테스트")
    class RejectTest {
        @Test
        @DisplayName("Pending 상태의 report로 reject 메서드를 호출하면 ReportResult가 REJECTED로 변경된다.")
        void changesReportResultToRejected() {
            // given
            Long adminId = 1L;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");

            // when
            report.reject(adminId);

            // then
            assertThat(report.getResult()).isEqualTo(ReportResult.REJECTED);
        }

        @Test
        @DisplayName("ReportResult가 PENDING이 아닌 경우 reject 메서드를 호출하면 예외가 발생한다.")
        void throwsExceptionWhenReportResultIsNotPending() {
            // given
            Long adminId = 1L;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");
            report.reject(adminId); // 상태를 REJECTED로 변경

            // when, then
            assertThatThrownBy(() -> report.reject(adminId)).isInstanceOf(InvalidReportResultException.class);
        }

        @Test
        @DisplayName("adminId가 null으로 reject 메서드를 호출하면 예외가 발생한다.")
        void throwsExceptionWhenAdminIdIsNull() {
            // given
            Long adminId = null;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");

            // when, then
            assertThatThrownBy(() -> report.reject(adminId)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("approve 메서드 테스트")
    class ApproveTest {
        @Test
        @DisplayName("Pending 상태의 report로 approve 메서드를 호출하면 ReportResult가 BANNED로 변경되고 이벤트를 발행한다.")
        void changesReportResultToApproved() {
            // given
            Long adminId = 1L;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");

            // when
            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                // when
                report.approve(adminId);

                // then
                eventsMockedStatic.verify(() -> Events.raise(argThat(
                    event -> event instanceof ReportApprovedEvent
                        && ((ReportApprovedEvent) event).getReporteeId() == report.getReporteeId()
                )), times(1));
                assertThat(report.getResult()).isEqualTo(ReportResult.BANNED);
            }
        }

        @Test
        @DisplayName("ReportResult가 PENDING이 아닌 경우 approve 메서드를 호출하면 예외가 발생한다.")
        void throwsExceptionWhenReportResultIsNotPending() {
            // given
            Long adminId = 1L;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");
            report.approve(adminId); // 상태를 APPROVED로 변경

            // when, then
            assertThatThrownBy(() -> report.approve(adminId)).isInstanceOf(InvalidReportResultException.class);
        }

        @Test
        @DisplayName("adminId가 null으로 approve 메서드를 호출하면 예외가 발생한다.")
        void throwsExceptionWhenAdminIdIsNull() {
            // given
            Long adminId = null;
            Report report = Report.of(1L, 2L, ReportReasonType.ETC, "content");

            // when, then
            assertThatThrownBy(() -> report.approve(adminId)).isInstanceOf(NullPointerException.class);
        }
    }
}