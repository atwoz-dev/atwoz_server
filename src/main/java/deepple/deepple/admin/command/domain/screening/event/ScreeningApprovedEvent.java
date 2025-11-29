package deepple.deepple.admin.command.domain.screening.event;

import deepple.deepple.common.event.Event;
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
