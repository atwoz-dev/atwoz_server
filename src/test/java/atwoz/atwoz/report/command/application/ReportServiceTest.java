package atwoz.atwoz.report.command.application;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.report.command.application.exception.ReportAlreadyExistsException;
import atwoz.atwoz.report.command.domain.ReportCommandRepository;
import atwoz.atwoz.report.command.domain.ReportReasonType;
import atwoz.atwoz.report.command.domain.exception.InvalidReportReasonTypeException;
import atwoz.atwoz.report.presentation.dto.ReportCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportCommandRepository reportCommandRepository;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Test
    @DisplayName("reportee가 존재하지 않으면 예외를 던진다.")
    void throwsExceptionWhenReporteeDoesNotExist() {
        // given
        long reporteeId = 1L;
        long reporterId = 2L;
        ReportReasonType reportReasonType = ReportReasonType.ETC;
        ReportCreateRequest request = new ReportCreateRequest(reporteeId, reportReasonType.name(), "content");

        // when, then
        assertThatThrownBy(() -> reportService.report(request, reporterId))
            .isInstanceOf(MemberNotFoundException.class);

        verify(reportCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("report가 이미 존재하면 예외를 던진다.")
    void throwsExceptionWhenReportAlreadyExists() {
        // given
        long reporteeId = 1L;
        long reporterId = 2L;
        ReportReasonType reportReasonType = ReportReasonType.ETC;
        ReportCreateRequest request = new ReportCreateRequest(reporteeId, reportReasonType.name(), "content");

        when(memberCommandRepository.existsById(reporteeId)).thenReturn(true);
        when(reportCommandRepository.existsByReporterIdAndReporteeId(reporterId, reporteeId)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> reportService.report(request, reporterId)).isInstanceOf(
            ReportAlreadyExistsException.class);

        verify(reportCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("reason이 틀린 값이면 예외를 던진다.")
    void throwsExceptionWhenReasonIsInvalid() {
        // given
        long reporteeId = 1L;
        long reporterId = 2L;
        String invalidReason = "INVALID_REASON";
        ReportCreateRequest request = new ReportCreateRequest(reporteeId, invalidReason, "content");

        when(memberCommandRepository.existsById(reporteeId)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> reportService.report(request, reporterId))
            .isInstanceOf(InvalidReportReasonTypeException.class);

        verify(reportCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("report 메서드가 정상적으로 호출되면 reportCommandRepository의 save 메서드가 호출된다.")
    void savesReportWhenReporteeExists() {
        // given
        long reporteeId = 1L;
        long reporterId = 2L;
        ReportReasonType reportReasonType = ReportReasonType.ETC;
        ReportCreateRequest request = new ReportCreateRequest(reporteeId, reportReasonType.name(), "content");

        when(memberCommandRepository.existsById(reporteeId)).thenReturn(true);

        // when
        reportService.report(request, reporterId);

        // then
        verify(reportCommandRepository, times(1)).save(argThat(reportCommand ->
            reportCommand.getReporterId() == reporterId &&
                reportCommand.getReporteeId() == reporteeId &&
                reportCommand.getReason() == reportReasonType &&
                reportCommand.getContent().equals(request.content())
        ));
    }


}