package atwoz.atwoz.report.query.view;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record ReportDetailView(
    Long id,
    Long version,
    Long reporterId,
    String reporterNickname,
    Long reporteeId,
    String reporteeNickname,
    String reason,
    String result,
    String content,
    LocalDateTime createdAt
) {
    @QueryProjection
    public ReportDetailView {
        // QueryDSL에서 사용하기 위한 생성자
    }
}
