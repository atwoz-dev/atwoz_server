package atwoz.atwoz.report.command.domain;

import atwoz.atwoz.report.command.domain.exception.InvalidReportResultException;
import lombok.NonNull;

public enum ReportResult {
    PENDING("대기"),
    REJECTED("기각"),
    WARNED("경고"),
    SUSPENDED("일시 정지");

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
