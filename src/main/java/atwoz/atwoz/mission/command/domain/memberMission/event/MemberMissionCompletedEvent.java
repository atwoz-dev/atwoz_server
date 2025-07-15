package atwoz.atwoz.mission.command.domain.memberMission.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberMissionCompletedEvent extends Event {

    private final long memberId;

    public static MemberMissionCompletedEvent from(long memberId) {
        return new MemberMissionCompletedEvent(memberId);
    }
}
