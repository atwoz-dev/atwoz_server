package atwoz.atwoz.notification.command.domain.notification.message.action;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequestedMessageTemplate implements MessageTemplate {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.LOGIN_REQUESTED;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        return "당신을 기다리고 있는 이성들을 확인하러 가볼까요? 장기간 접속이 없으면 휴면계정으로 전환돼요.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
