package atwoz.atwoz.admin.command.domain.screening.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScreeningRejectedEvent extends Event {
    private final long memberId;

    public static ScreeningRejectedEvent from(long memberId) {
        return new ScreeningRejectedEvent(memberId);
    }
}
