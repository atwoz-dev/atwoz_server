package atwoz.atwoz.admin.domain.application.exception;

public class DuplicateAdminException extends RuntimeException {
    public DuplicateAdminException(String message) {
        super(message);
    }
}
