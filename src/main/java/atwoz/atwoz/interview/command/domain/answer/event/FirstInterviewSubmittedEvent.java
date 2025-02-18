package atwoz.atwoz.interview.command.domain.answer.event;

import atwoz.atwoz.common.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FirstInterviewSubmittedEvent extends Event {

    private final Long memberId;

    public static FirstInterviewSubmittedEvent from(Long memberId) {
        return new FirstInterviewSubmittedEvent(memberId);
    }
}
