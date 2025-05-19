package atwoz.atwoz.report.command.domain.exception;

public class InvalidReportReasonTypeException extends RuntimeException {
    public InvalidReportReasonTypeException(String value) {
        super("Invalid report reason type: " + value);
    }
}
