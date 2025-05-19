package atwoz.atwoz.report.command.domain;

public enum ReportResult {
    PENDING("대기"),
    REJECTED("기각"),
    BANNED("정지");

    private final String description;

    ReportResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
