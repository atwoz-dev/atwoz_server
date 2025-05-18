package atwoz.atwoz.report.command.application;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.report.command.domain.Report;
import atwoz.atwoz.report.command.domain.ReportCommandRepository;
import atwoz.atwoz.report.command.domain.ReportReasonType;
import atwoz.atwoz.report.presentation.dto.ReportRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportCommandRepository reportCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public void report(ReportRequest request, long reporterId) {
        validateRequest(request);
        ReportReasonType reportReasonType = ReportReasonType.from(request.reason());
        Report report = Report.of(reporterId, request.reporteeId(), reportReasonType, request.content());
        reportCommandRepository.save(report);
    }

    private void validateRequest(ReportRequest request) {
        if (!memberCommandRepository.existsById(request.reporteeId())) {
            throw new MemberNotFoundException();
        }
    }
}
