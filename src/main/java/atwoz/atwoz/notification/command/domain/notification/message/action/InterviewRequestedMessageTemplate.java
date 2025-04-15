package atwoz.atwoz.notification.command.domain.notification.message.action;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewRequestedMessageTemplate implements MessageTemplate {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.INTERVIEW_REQUESTED;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        return "아직 프로필 소개 글을 작성하지 않으셨네요! 인터뷰를 작성하시면 무료 하트를 지급해 드립니다.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
