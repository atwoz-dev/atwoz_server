package atwoz.atwoz.like.command.domain.like.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCreatedEvent extends Event {
    private final Long senderId;
    private final Long receiverId;

    public static LikeCreatedEvent of(Long senderId, Long receiverId) {
        return new LikeCreatedEvent(senderId, receiverId);
    }
}
