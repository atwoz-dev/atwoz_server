package deepple.deepple.report.command.domain;

import deepple.deepple.report.command.domain.exception.InvalidReportResultException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "ReportResult",
    description = "신고 결과",
    type = "string",
    example = "PENDING"
)
public enum ReportResult {
    PENDING("대기"),
    REJECTED("기각"),
    WARNED("경고");

    private final String description;

    ReportResult(String description) {
        this.description = description;
    }

    public static ReportResult from(@NonNull String result) {
        try {
            return ReportResult.valueOf(result.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidReportResultException("Invalid report result: " + result);
        }
    }

    public String getDescription() {
        return description;
    }
}
