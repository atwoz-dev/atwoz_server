package atwoz.atwoz.admin.command.domain.screening.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScreeningRejectedEvent extends Event {
    private final long adminId;
    private final long memberId;
    private final String rejectionReason;

    public static ScreeningRejectedEvent of(long adminId, long memberId, String rejectionReason) {
        return new ScreeningRejectedEvent(adminId, memberId, rejectionReason);
    }
}
