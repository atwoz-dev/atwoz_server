package atwoz.atwoz.report.command.application.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException() {
        super("Report not found");
    }
}
