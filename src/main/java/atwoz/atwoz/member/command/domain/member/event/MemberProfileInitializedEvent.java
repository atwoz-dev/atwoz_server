package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileInitializedEvent extends Event {
    private final Long memberId;

    public static MemberProfileInitializedEvent from(@NonNull Long memberId) {
        return new MemberProfileInitializedEvent(memberId);
    }
}
