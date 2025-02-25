package atwoz.atwoz.match.command.domain.match.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRequestedEvent extends Event {
    private final Long requesterId;
    private final Long responderId;

    public static MatchRequestedEvent of(Long requesterId, Long responderId) {
        return new MatchRequestedEvent(requesterId, responderId);
    }
}
