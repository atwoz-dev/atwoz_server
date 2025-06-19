package atwoz.atwoz.admin.command.domain.screening.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScreeningRejectedEvent extends Event {

    private final Long memberId;

    public static ScreeningRejectedEvent of(Long memberId) {
        return new ScreeningRejectedEvent(memberId);
    }
}
