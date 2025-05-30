package atwoz.atwoz.admin.command.application.warning;

public class InvalidWarningReasonTypeException extends RuntimeException {
    public InvalidWarningReasonTypeException(String warningReasonType) {
        super(warningReasonType + "은(는) 유효하지 않은 경고 사유입니다.");
    }
}
