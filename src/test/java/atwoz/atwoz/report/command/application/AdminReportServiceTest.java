package atwoz.atwoz.report.command.application;

import atwoz.atwoz.report.command.application.exception.ReportNotFoundException;
import atwoz.atwoz.report.command.domain.Report;
import atwoz.atwoz.report.command.domain.ReportCommandRepository;
import atwoz.atwoz.report.presentation.dto.ReportApproveRequest;
import atwoz.atwoz.report.presentation.dto.ReportRejectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReportServiceTest {

    @Mock
    private ReportCommandRepository reportCommandRepository;

    @Mock
    private Report report;

    @InjectMocks
    private AdminReportService adminReportService;

    @Nested
    @DisplayName("approve 메서드")
    class ApproveTests {

        @Test
        @DisplayName("존재하지 않는 신고를 승인하면 ReportNotFoundException을 던진다")
        void shouldThrowReportNotFoundExceptionWhenReportNotFound() {
            // given
            long reportId = 1L;
            ReportApproveRequest request = new ReportApproveRequest(0L);
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminReportService.approve(reportId, request, 100L))
                .isInstanceOf(ReportNotFoundException.class);

            verify(reportCommandRepository).findById(reportId);
            verify(report, never()).hasVersionConflict(anyLong());
            verify(report, never()).approve(anyLong());
        }

        @Test
        @DisplayName("버전이 일치하지 않으면 OptimisticLockingFailureException을 던진다")
        void shouldThrowOptimisticLockingFailureExceptionWhenVersionConflict() {
            // given
            long reportId = 2L;
            ReportApproveRequest request = new ReportApproveRequest(5L);
            long adminId = 200L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));
            when(report.hasVersionConflict(request.version())).thenReturn(true);

            // when & then
            assertThatThrownBy(() ->
                adminReportService.approve(reportId, request, adminId)
            ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessage("신고를 처리할 수 없습니다.");

            verify(reportCommandRepository).findById(reportId);
            verify(report).hasVersionConflict(request.version());
            verify(report, never()).approve(anyLong());
        }

        @Test
        @DisplayName("유효한 요청이면 report.approve를 호출한다")
        void shouldApproveReportWhenValid() {
            // given
            long reportId = 3L;
            ReportApproveRequest request = new ReportApproveRequest(1L);
            long adminId = 300L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));
            when(report.hasVersionConflict(request.version())).thenReturn(false);

            // when
            adminReportService.approve(reportId, request, adminId);

            // then
            verify(reportCommandRepository).findById(reportId);
            verify(report).hasVersionConflict(request.version());
            verify(report).approve(adminId);
        }

        @Test
        @DisplayName("요청 객체가 null이면 NullPointerException을 던진다")
        void shouldThrowNullPointerExceptionWhenRequestIsNull() {
            // given
            long reportId = 4L;
            long adminId = 400L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));

            // when & then
            assertThatThrownBy(() ->
                adminReportService.approve(reportId, null, adminId)
            ).isInstanceOf(NullPointerException.class);

            verify(reportCommandRepository).findById(reportId);
            verify(report, never()).hasVersionConflict(anyLong());
            verify(report, never()).approve(anyLong());
        }
    }

    @Nested
    @DisplayName("reject 메서드")
    class RejectTests {

        @Test
        @DisplayName("존재하지 않는 신고를 거부하면 ReportNotFoundException을 던진다")
        void shouldThrowReportNotFoundExceptionWhenReportNotFound() {
            // given
            long reportId = 5L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.empty());
            ReportRejectRequest request = new ReportRejectRequest(0L);

            // when & then
            assertThatThrownBy(() ->
                adminReportService.reject(reportId, request, 101L)
            ).isInstanceOf(ReportNotFoundException.class);

            verify(reportCommandRepository).findById(reportId);
            verify(report, never()).hasVersionConflict(anyLong());
            verify(report, never()).reject(anyLong());
        }

        @Test
        @DisplayName("버전이 일치하지 않으면 OptimisticLockingFailureException을 던진다")
        void shouldThrowOptimisticLockingFailureExceptionWhenVersionConflict() {
            // given
            long reportId = 6L;
            ReportRejectRequest request = new ReportRejectRequest(5L);
            long adminId = 202L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));
            when(report.hasVersionConflict(request.version())).thenReturn(true);

            // when & then
            assertThatThrownBy(() ->
                adminReportService.reject(reportId, request, adminId)
            ).isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessage("신고를 처리할 수 없습니다.");

            verify(reportCommandRepository).findById(reportId);
            verify(report).hasVersionConflict(request.version());
            verify(report, never()).reject(anyLong());
        }

        @Test
        @DisplayName("유효한 요청이면 report.reject를 호출한다")
        void shouldRejectReportWhenValid() {
            // given
            long reportId = 7L;
            ReportRejectRequest request = new ReportRejectRequest(1L);
            long adminId = 303L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));
            when(report.hasVersionConflict(request.version())).thenReturn(false);

            // when
            adminReportService.reject(reportId, request, adminId);

            // then
            verify(reportCommandRepository).findById(reportId);
            verify(report).hasVersionConflict(request.version());
            verify(report).reject(adminId);
        }

        @Test
        @DisplayName("요청 객체가 null이면 NullPointerException을 던진다")
        void shouldThrowNullPointerExceptionWhenRequestIsNull() {
            // given
            long reportId = 8L;
            long adminId = 404L;
            when(reportCommandRepository.findById(reportId)).thenReturn(Optional.of(report));

            // when & then
            assertThatThrownBy(() ->
                adminReportService.reject(reportId, null, adminId)
            ).isInstanceOf(NullPointerException.class);

            verify(reportCommandRepository).findById(reportId);
            verify(report, never()).hasVersionConflict(anyLong());
            verify(report, never()).reject(anyLong());
        }
    }
}
