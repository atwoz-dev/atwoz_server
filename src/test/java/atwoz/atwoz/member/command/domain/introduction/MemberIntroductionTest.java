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
import static org.mockito.Mockito.*;

class MemberIntroductionTest {

    @ParameterizedTest
    @ValueSource(strings = {"memberId", "introducedMemberId", "content"})
    @DisplayName("null 값을 가지고 생성하면 예외를 던진다.")
    void throwsExceptionWhenNullValue(String nullParam) {
        // given
        Long memberId = nullParam.equals("memberId") ? null : 1L;
        Long introducedMemberId = nullParam.equals("introducedMemberId") ? null : 2L;
        IntroductionType type = nullParam.equals("content") ? null : mock(IntroductionType.class);

        // when & then
        assertThatThrownBy(() -> MemberIntroduction.of(memberId, introducedMemberId, type))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("유료 타입으로 소개를 생성하면 MemberIntroducedEvent를 발행하고 MemberIntroduction을 생성한다.")
    void createIntroduction() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;
        IntroductionType type = mock(IntroductionType.class);
        when(type.isFreeIntroduction()).thenReturn(false);
        when(type.getDescription()).thenReturn("diamond");

        try (MockedStatic<Events> eventsMock = mockStatic(Events.class);
            MockedStatic<MemberIntroducedEvent> memberIntroducedEventMock = mockStatic(MemberIntroducedEvent.class)
        ) {
            MemberIntroducedEvent memberIntroducedEvent = mock(MemberIntroducedEvent.class);
            memberIntroducedEventMock.when(() -> MemberIntroducedEvent.of(memberId, type.getDescription()))
                .thenReturn(memberIntroducedEvent);

            // when
            MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId, type);

            // then
            eventsMock.verify(() -> Events.raise(memberIntroducedEvent));
            assertThat(memberIntroduction.getMemberId()).isEqualTo(memberId);
            assertThat(memberIntroduction.getIntroducedMemberId()).isEqualTo(introducedMemberId);
        }
    }

    @Test
    @DisplayName("무료 소개 타입인 경우 이벤틀르 발행하지 않는다.")
    void createIntroductionWithFreeType() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;
        IntroductionType type = mock(IntroductionType.class);
        when(type.isFreeIntroduction()).thenReturn(true);
        when(type.getDescription()).thenReturn("free");

        try (MockedStatic<Events> eventsMock = mockStatic(Events.class)) {
            // when
            MemberIntroduction memberIntroduction = MemberIntroduction.of(memberId, introducedMemberId, type);

            // then
            eventsMock.verifyNoInteractions();
            assertThat(memberIntroduction.getMemberId()).isEqualTo(memberId);
            assertThat(memberIntroduction.getIntroducedMemberId()).isEqualTo(introducedMemberId);
        }
    }
}