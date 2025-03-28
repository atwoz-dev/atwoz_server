package atwoz.atwoz.notification.command.domain.notification.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageTemplateParameters {
    private final Long senderId;
    private final Long receiverId;

    public static MessageTemplateParameters of(Long senderId, Long receiverId) {
        return new MessageTemplateParameters(senderId, receiverId);
    }
}
