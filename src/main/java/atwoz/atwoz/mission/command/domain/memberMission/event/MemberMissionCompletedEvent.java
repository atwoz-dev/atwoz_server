package atwoz.atwoz.mission.command.domain.memberMission.event;

import atwoz.atwoz.common.event.Events;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberMissionCompletedEvent extends Events {

    private final long memberId;

    public static MemberMissionCompletedEvent from(long memberId) {
        return new MemberMissionCompletedEvent(memberId);
    }
}
