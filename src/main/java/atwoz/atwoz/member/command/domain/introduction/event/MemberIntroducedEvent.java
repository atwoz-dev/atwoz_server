package atwoz.atwoz.member.command.domain.introduction.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberIntroducedEvent extends Event {
    private final Long memberId;

    public static MemberIntroducedEvent of(Long memberId) {
        return new MemberIntroducedEvent(memberId);
    }
}
