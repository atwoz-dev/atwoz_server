package atwoz.atwoz.mission.command.domain.memberMission.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberMissionCompletedEvent extends Event {
    private final long memberId;
    private final long rewardHeartAmount;
    private final String memberNickname;
    private final String actionType;

    public static MemberMissionCompletedEvent from(long memberId, String memberNickname, long rewardedHeart,
        String actionType) {
        return new MemberMissionCompletedEvent(memberId, rewardedHeart, memberNickname, actionType);
    }
}
