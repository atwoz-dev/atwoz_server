package atwoz.atwoz.match.command.domain.match.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRequestedEvent extends Event {
    private final long requesterId;
    private final String requesterName;
    private final long responderId;
    private final String matchType;

    public static MatchRequestedEvent of(long requesterId, @NonNull String requesterName, long responderId,
        String matchType) {
        return new MatchRequestedEvent(requesterId, requesterName, responderId, matchType);
    }
}
