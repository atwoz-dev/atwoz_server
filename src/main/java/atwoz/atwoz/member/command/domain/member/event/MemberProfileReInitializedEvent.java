package atwoz.atwoz.member.command.domain.member.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileReInitializedEvent extends Event {
    private final Long memberId;

    public static MemberProfileReInitializedEvent from(@NonNull Long memberId) {
        return new MemberProfileReInitializedEvent(memberId);
    }
}
