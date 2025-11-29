package deepple.deepple.report.command.application.exception;

public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException() {
        super("Report already exists.");
    }
}
