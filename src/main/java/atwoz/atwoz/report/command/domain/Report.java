package atwoz.atwoz.report.command.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.report.command.domain.event.ReportCreatedEvent;
import atwoz.atwoz.report.command.domain.exception.InvalidReportResultException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private Long reporterId;

    @Getter
    private Long reporteeId;

    @Getter
    private Long adminId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ReportReasonType reason;

    @Getter
    private String content;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ReportResult result;

    private Report(long reporterId, long reporteeId, Long adminId, ReportReasonType reason, String content,
        ReportResult status) {
        setReporterId(reporterId);
        setReporteeId(reporteeId);
        setAdminId(adminId);
        setReason(reason);
        setContent(content);
        setStatus(status);
    }

    public static Report of(long reporterId, long reporteeId, ReportReasonType reason, String content) {
        Report report = new Report(reporterId, reporteeId, null, reason, content, ReportResult.PENDING);
        Events.raise(new ReportCreatedEvent(reporterId, reporteeId));
        return report;
    }

    public void reject(long adminId) {
        validateResult();
        setAdminId(adminId);
        setStatus(ReportResult.REJECTED);
    }

    public void approve(long adminId) {
        validateResult();
        setAdminId(adminId);
        setStatus(ReportResult.BANNED);
    }

    private void validateResult() {
        if (this.result != ReportResult.PENDING) {
            throw new InvalidReportResultException("이미 처리된 신고입니다.");
        }
    }

    private void setReporterId(long reporterId) {
        this.reporterId = reporterId;
    }

    private void setReporteeId(long reporteeId) {
        this.reporteeId = reporteeId;
    }

    private void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    private void setReason(@NonNull ReportReasonType reason) {
        this.reason = reason;
    }

    private void setContent(@NonNull String content) {
        this.content = content;
    }

    private void setStatus(@NonNull ReportResult result) {
        this.result = result;
    }
}
