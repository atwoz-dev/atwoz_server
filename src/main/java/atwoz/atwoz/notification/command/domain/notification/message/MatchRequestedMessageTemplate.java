package atwoz.atwoz.notification.command.domain.notification.message;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRequestedMessageTemplate implements MessageTemplate {

    private final String receiverName;

    public static MatchRequestedMessageTemplate from(String receiverName) {
        return new MatchRequestedMessageTemplate(receiverName);
    }

    @Override
    public String getTitle() {
        return receiverName + "님께 매치가 요청되었습니다.";
    }

    @Override
    public String getContent() {
        return null;
    }
}
