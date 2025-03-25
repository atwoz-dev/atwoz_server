package atwoz.atwoz.notification.command.domain.notification.message;

public class DefaultMessageTemplate implements MessageTemplate {

    @Override
    public String getTitle() {
        return "알림이 전송되었습니다.";
    }

    @Override
    public String getContent() {
        return null;
    }
}
