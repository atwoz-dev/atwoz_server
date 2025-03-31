package atwoz.atwoz.notification.command.domain.notification.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageTemplateParameters {
    private final long senderId;
    private final long receiverId;

    public static MessageTemplateParameters of(long senderId, long receiverId) {
        return new MessageTemplateParameters(senderId, receiverId);
    }
}
