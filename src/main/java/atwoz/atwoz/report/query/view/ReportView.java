package atwoz.atwoz.report.query.view;

import atwoz.atwoz.report.command.domain.ReportReasonType;
import atwoz.atwoz.report.command.domain.ReportResult;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReportView(
    Long id,
    Long reporterId,
    String reporterNickname,
    Long reporteeId,
    String reporteeNickname,
    @Schema(implementation = ReportReasonType.class)
    String reason,
    @Schema(implementation = ReportResult.class)
    String result,
    LocalDateTime createdAt
) {
    @QueryProjection
    public ReportView {
        // QueryDSL에서 사용하기 위한 생성자
    }
}
