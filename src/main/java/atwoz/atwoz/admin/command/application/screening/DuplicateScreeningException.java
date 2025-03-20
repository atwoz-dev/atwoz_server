package atwoz.atwoz.admin.command.application.screening;

public class DuplicateScreeningException extends RuntimeException {
    public DuplicateScreeningException(long memberId) {
        super("멤버(id: " + memberId + ")에 대해 중복된 Screening을 생성할 수 없습니다.");
    }
}
