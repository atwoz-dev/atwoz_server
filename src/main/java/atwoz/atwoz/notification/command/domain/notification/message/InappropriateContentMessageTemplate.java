package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;

public class InappropriateContentMessageTemplate implements MessageTemplate {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.INAPPROPRIATE_CONTENT;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        return "작성하신 게시글에 부적절한 내용이 포함되어 있습니다. 다른 사용자들에게 불쾌감을 줄 수 있는 게시글은 삭제될 수 있습니다.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
