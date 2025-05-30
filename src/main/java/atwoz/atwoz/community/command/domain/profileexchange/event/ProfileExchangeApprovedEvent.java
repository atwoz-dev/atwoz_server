package atwoz.atwoz.community.command.domain.profileexchange.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileExchangeApprovedEvent extends Event {
    private final long requesterId;
    private final long responderId;
    private final String senderName;

    public static ProfileExchangeApprovedEvent of(long requesterId, long responderId, String senderName) {
        return new ProfileExchangeApprovedEvent(requesterId, responderId, senderName);
    }
}
