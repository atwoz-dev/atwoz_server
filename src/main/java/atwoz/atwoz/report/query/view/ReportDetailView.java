package atwoz.atwoz.report.query.view;

import atwoz.atwoz.report.command.domain.ReportReasonType;
import atwoz.atwoz.report.command.domain.ReportResult;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReportDetailView(
    Long id,
    Long version,
    Long reporterId,
    String reporterNickname,
    Long reporteeId,
    String reporteeNickname,
    @Schema(implementation = ReportReasonType.class)
    String reason,
    @Schema(implementation = ReportResult.class)
    String result,
    String content,
    LocalDateTime createdAt
) {
    @QueryProjection
    public ReportDetailView {
        // QueryDSL에서 사용하기 위한 생성자
    }
}
