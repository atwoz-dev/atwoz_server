package atwoz.atwoz.notification.command.domain.notification.message.admin;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileImageChangeRejectedMessageTemplate implements MessageTemplate {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.PROFILE_IMAGE_CHANGE_REQUESTED;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        return "나를 보다 더 표현할 수 있는 프로필 사진으로 변경해보세요. 프로필 사진만으로도 첫인상이 달라져요.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
