package atwoz.atwoz.report.command.application;

import atwoz.atwoz.report.command.application.exception.ReportNotFoundException;
import atwoz.atwoz.report.command.domain.Report;
import atwoz.atwoz.report.command.domain.ReportCommandRepository;
import atwoz.atwoz.report.presentation.dto.ReportApproveRequest;
import atwoz.atwoz.report.presentation.dto.ReportRejectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final ReportCommandRepository reportCommandRepository;

    @Transactional
    public void approve(long reportId, ReportApproveRequest request, long adminId) {
        Report report = getReport(reportId);
        validateReport(report, request.version());
        report.approve(adminId);
    }

    @Transactional
    public void reject(long reportId, ReportRejectRequest request, long adminId) {
        Report report = getReport(reportId);
        validateReport(report, request.version());
        report.reject(adminId);
    }

    private Report getReport(long id) {
        return reportCommandRepository.findById(id).orElseThrow(ReportNotFoundException::new);
    }

    private void validateReport(Report report, long version) {
        if (report.hasVersionConflict(version)) {
            throw new OptimisticLockingFailureException("신고를 처리할 수 없습니다.");
        }
    }
}
