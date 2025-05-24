package atwoz.atwoz.community.command.domain.selfintroduction.event;

import atwoz.atwoz.common.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SelfIntroductionOpenStatusChangeEvent extends Event {
    private final Long id;
    private final boolean open;

    public static SelfIntroductionOpenStatusChangeEvent of(long id, boolean open) {
        return new SelfIntroductionOpenStatusChangeEvent(id, open);
    }

}
