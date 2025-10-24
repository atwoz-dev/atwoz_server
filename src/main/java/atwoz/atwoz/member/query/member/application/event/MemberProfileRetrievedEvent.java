package atwoz.atwoz.member.query.member.application.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileRetrievedEvent extends Event {

    private final Long retrieverId;
    private final Long retrievedMemberId;
    private final Long matchRequesterId;
    private final Long matchResponderId;

    public static MemberProfileRetrievedEvent of(
        @NonNull Long retrieverId,
        @NonNull Long retrievedMemberId,
        Long matchRequesterId,
        Long matchResponderId
    ) {
        return new MemberProfileRetrievedEvent(retrieverId, retrievedMemberId, matchRequesterId, matchResponderId);
    }
}
