package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class MemberSettingUpdatedEvent extends Event {
    private final long memberId;
    private final boolean isPushNotificationEnabled;

    public static MemberSettingUpdatedEvent of(long memberId, boolean isPushNotificationEnabled) {
        return new MemberSettingUpdatedEvent(memberId, isPushNotificationEnabled);
    }
}
