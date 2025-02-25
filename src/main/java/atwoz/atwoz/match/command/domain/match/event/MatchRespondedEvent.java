package atwoz.atwoz.match.command.domain.match.event;

import atwoz.atwoz.common.event.Event;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRespondedEvent extends Event {
    Long requesterId;
    Long responderId;
    MatchStatus matchStatus;

    public static MatchRespondedEvent of(Long requesterId, Long responderId, MatchStatus matchStatus) {
        return new MatchRespondedEvent(requesterId, responderId, matchStatus);
    }
}
