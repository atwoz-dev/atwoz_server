package deepple.deepple.member.command.domain.member.event;

import deepple.deepple.common.event.Event;
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
