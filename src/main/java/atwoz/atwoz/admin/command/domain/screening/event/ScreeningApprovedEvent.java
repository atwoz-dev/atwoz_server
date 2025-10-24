package atwoz.atwoz.admin.command.domain.screening.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScreeningApprovedEvent extends Event {
    private final long adminId;
    private final long memberId;

    public static ScreeningApprovedEvent of(long adminId, long memberId) {
        return new ScreeningApprovedEvent(adminId, memberId);
    }
}
