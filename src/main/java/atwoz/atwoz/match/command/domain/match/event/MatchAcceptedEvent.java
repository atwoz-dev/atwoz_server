package atwoz.atwoz.match.command.domain.match.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchAcceptedEvent extends Event {
    private final long requesterId;
    private final long responderId;
    private final String responderName;

    public static MatchAcceptedEvent of(Long requesterId, Long responderId, String responderName) {
        return new MatchAcceptedEvent(requesterId, responderId, responderName);
    }
}