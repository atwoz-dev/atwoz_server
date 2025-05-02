package atwoz.atwoz.member.command.domain.introduction;


import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.domain.introduction.event.MemberIntroducedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class MemberIntroductionTest {

    @ParameterizedTest
    @ValueSource(strings = {"memberId", "introducedMemberId"})
    @DisplayName("null 값을 가지고 생성하면 예외를 던진다.")
    void throwsExceptionWhenNullValue(String nullParam) {
        // given
        Long memberId = nullParam.equals("memberId") ? null : 1L;
        Long introducedMemberId = nullParam.equals("introducedMemberId") ? null : 2L;

        // when & then
        assertThatThrownBy(() -> MemberIntroduction.of(memberId, introducedMemberId))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("of 메서드를 호출하면 MemberIntroducedEvent를 발행하고 MemberIntroduction을 생성한다.")
    void createIntroduction() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;


        try (MockedStatic<Events> eventsMock = mockStatic(Events.class);
            MockedStatic<MemberIntroducedEvent> memberIntroducedEventMock = mockStatic(MemberIntroducedEvent.class)
        ) {
            MemberIntroducedEvent memberIntroducedEvent = mock(MemberIntroducedEvent.class);
            memberIntroducedEventMock.when(() -> MemberIntroducedEvent.of(memberId)).thenReturn(memberIntroducedEvent);

            // when
            MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId);

            // then
            eventsMock.verify(() -> Events.raise(memberIntroducedEvent));
            assertThat(memberIntroduction.getMemberId()).isEqualTo(memberId);
            assertThat(memberIntroduction.getIntroducedMemberId()).isEqualTo(introducedMemberId);
        }
    }
}