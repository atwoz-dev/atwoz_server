package deepple.deepple.admin.command.application.screening;

public class ScreeningNotFoundException extends RuntimeException {
    public ScreeningNotFoundException() {
        super("심사를 찾을 수 없습니다.");
    }
}
