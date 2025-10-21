package atwoz.atwoz.admin.command.domain.screening.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScreeningWaitingEvent extends Event {
    private final long memberId;

    public static ScreeningWaitingEvent from(long memberId) {
        return new ScreeningWaitingEvent(memberId);
    }
}
