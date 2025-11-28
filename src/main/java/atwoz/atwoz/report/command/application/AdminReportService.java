package atwoz.atwoz.report.command.application;

import atwoz.atwoz.report.command.application.exception.ReportNotFoundException;
import atwoz.atwoz.report.command.domain.Report;
import atwoz.atwoz.report.command.domain.ReportCommandRepository;
import atwoz.atwoz.report.command.domain.ReportResult;
import atwoz.atwoz.report.command.domain.exception.InvalidReportResultException;
import atwoz.atwoz.report.presentation.dto.ReportResultUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final ReportCommandRepository reportCommandRepository;

    @Transactional
    public void updateResult(final long reportId, final ReportResultUpdateRequest request, final long adminId) {
        Report report = getReport(reportId);
        validateReport(report, request.version());
        setReportResult(report, request, adminId);
    }

    private Report getReport(long id) {
        return reportCommandRepository.findById(id).orElseThrow(ReportNotFoundException::new);
    }

    private void validateReport(Report report, long version) {
        if (report.hasVersionConflict(version)) {
            throw new OptimisticLockingFailureException("신고를 처리할 수 없습니다.");
        }
    }

    private void setReportResult(Report report, ReportResultUpdateRequest request, long adminId) {
        ReportResult reportResult = ReportResult.from(request.result());
        switch (reportResult) {
            case REJECTED -> report.reject(adminId);
            case WARNED -> report.warn(adminId);
            case PENDING -> throw new InvalidReportResultException("PENDING 으로 결과를 설정할 수 없습니다.");
            default -> throw new InvalidReportResultException("Invalid report result: " + request.result());
        }
    }
}
