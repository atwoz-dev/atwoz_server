package atwoz.atwoz.notification.command.domain.notification.strategy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRequestedMessageStrategy implements NotificationMessageStrategy {

    private final String receiverName;

    public static MatchRequestedMessageStrategy from(String receiverName) {
        return new MatchRequestedMessageStrategy(receiverName);
    }

    @Override
    public String createTitle() {
        return receiverName + "님께 매치가 요청되었습니다.";
    }

    @Override
    public String createContent() {
        return null;
    }
}
