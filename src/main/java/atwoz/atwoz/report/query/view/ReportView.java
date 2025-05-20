package atwoz.atwoz.report.query.view;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record ReportView(
    Long id,
    Long reporterId,
    String reporterNickname,
    Long reporteeId,
    String reporteeNickname,
    String reason,
    String result,
    LocalDateTime createdAt
) {
    @QueryProjection
    public ReportView {
        // QueryDSL에서 사용하기 위한 생성자
    }
}
