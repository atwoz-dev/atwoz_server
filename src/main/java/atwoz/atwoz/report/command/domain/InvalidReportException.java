package atwoz.atwoz.report.command.domain;

public class InvalidReportException extends RuntimeException {
    public InvalidReportException(String message) {
        super(message);
    }
}
