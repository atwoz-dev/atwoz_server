package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.IntroductionType;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodayCardServiceTest {

    @InjectMocks
    private TodayCardService todayCardService;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private MemberIntroductionCommandRepository memberIntroductionCommandRepository;

    @Test
    @DisplayName("오늘의 카드 멤버 id가 이미 소개된 멤버라면 소개를 생성하지 않는다")
    void doesNotCreateIntroductionWhenAlreadyExists() {
        // given
        final long memberId = 1L;
        final long todayCardMemberId = 2L;
        Set<Long> todayCardMemberIds = Set.of(todayCardMemberId);

        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, todayCardMemberId))
            .thenReturn(true);

        // when
        todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

        // then
        verify(memberIntroductionCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("오늘의 카드 멤버 id가 존재하지 않으면 예외를 던진다.")
    void throwsExceptionWhenTodayCardMemberIdNotFound() {
        // given
        final long memberId = 1L;
        final long todayCardMemberId = 2L;
        Set<Long> todayCardMemberIds = Set.of(todayCardMemberId);

        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, todayCardMemberId))
            .thenReturn(false);
        when(memberCommandRepository.findById(todayCardMemberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds))
            .isInstanceOf(IntroducedMemberNotFoundException.class);
    }

    @Test
    @DisplayName("오늘의 카드 멤버 id가 Active 상태가 아니라면 예외를 던진다")
    void throwsExceptionWhenTodayCardMemberNotActive() {
        // given
        final long memberId = 1L;
        final long todayCardMemberId = 2L;
        Set<Long> todayCardMemberIds = Set.of(todayCardMemberId);

        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, todayCardMemberId))
            .thenReturn(false);
        Member introducedMember = mock(Member.class);
        when(introducedMember.isActive()).thenReturn(false);
        when(memberCommandRepository.findById(todayCardMemberId)).thenReturn(Optional.of(introducedMember));

        // when & then
        assertThatThrownBy(() -> todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds))
            .isInstanceOf(IntroducedMemberNotActiveException.class);
    }


    @Test
    @DisplayName("오늘의 카드 멤버 id가 유효하다면 소개를 생성한다")
    void createTodayCardIntroduction() {
        // given
        final long memberId = 1L;
        final long todayCardMemberId = 2L;
        Set<Long> todayCardMemberIds = Set.of(todayCardMemberId);

        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId, todayCardMemberId))
            .thenReturn(false);

        Member introducedMember = mock(Member.class);
        when(introducedMember.isActive()).thenReturn(true);
        when(memberCommandRepository.findById(todayCardMemberId)).thenReturn(Optional.of(introducedMember));

        // when
        try (MockedStatic<MemberIntroduction> memberIntroductionMockedStatic = mockStatic(MemberIntroduction.class)) {
            final MemberIntroduction memberIntroduction = mock(MemberIntroduction.class);
            memberIntroductionMockedStatic.when(
                    () -> MemberIntroduction.of(memberId, todayCardMemberId, IntroductionType.TODAY_CARD))
                .thenReturn(memberIntroduction);
            todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

            // then
            verify(memberIntroductionCommandRepository, times(1)).save(memberIntroduction);
        }
    }
}