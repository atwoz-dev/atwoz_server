package atwoz.atwoz.member.command.application.introduction;

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

import java.util.List;
import java.util.Set;

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
    @DisplayName("오늘의 카드 멤버 id가 모두 이미 소개된 멤버라면 소개를 생성하지 않는다")
    void doesNotCreateIntroductionWhenAlreadyExists() {
        // given
        final long memberId = 1L;
        Set<Long> todayCardMemberIds = Set.of(2L, 3L);

        when(memberIntroductionCommandRepository.findIntroducedMemberIdsByMemberIdAndIntroducedMemberIdIn(memberId,
            todayCardMemberIds))
            .thenReturn(todayCardMemberIds);

        // when
        todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

        // then
        verify(memberCommandRepository, never()).findAllById(any());
        verify(memberIntroductionCommandRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("오늘의 카드 멤버 id 중에 소개되지 않은 멤버가 존재하지 않는다면 소개를 생성하지 않는다")
    void doesNotCreateIntroductionWhenNoIntroducedMembers() {
        // given
        final long memberId = 1L;
        Set<Long> todayCardMemberIds = Set.of(2L, 3L);

        when(memberIntroductionCommandRepository.findIntroducedMemberIdsByMemberIdAndIntroducedMemberIdIn(memberId,
            todayCardMemberIds))
            .thenReturn(Set.of());
        when(memberCommandRepository.findAllById(todayCardMemberIds)).thenReturn(List.of());

        // when
        todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

        // then
        verify(memberIntroductionCommandRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("오늘의 카드 멤버 id 중에 소개되지 않은 멤버 비활성화되어 있다면 소개를 생성하지 않는다")
    void doesNotCreateIntroductionWhenIntroducedMemberIsInactive() {
        // given
        final long memberId = 1L;
        Set<Long> todayCardMemberIds = Set.of(2L, 3L, 4L);

        when(memberIntroductionCommandRepository.findIntroducedMemberIdsByMemberIdAndIntroducedMemberIdIn(memberId,
            todayCardMemberIds))
            .thenReturn(Set.of());

        Member inactiveMember = mock(Member.class);
        when(inactiveMember.isActive()).thenReturn(false);

        when(memberCommandRepository.findAllById(todayCardMemberIds)).thenReturn(List.of(inactiveMember));

        // when
        todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

        // then
        verify(memberIntroductionCommandRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("오늘의 카드 멤버 id 중에 소개되지 않은 멤버가 active 상태라면 소개를 생성한다")
    void createsIntroductionWhenIntroducedMemberIsActive() {
        // given
        final long memberId = 1L;
        Set<Long> todayCardMemberIds = Set.of(2L, 3L);

        when(memberIntroductionCommandRepository.findIntroducedMemberIdsByMemberIdAndIntroducedMemberIdIn(memberId,
            todayCardMemberIds))
            .thenReturn(Set.of());

        Member activeMember = mock(Member.class);
        when(activeMember.isActive()).thenReturn(true);
        when(activeMember.getId()).thenReturn(2L);

        when(memberCommandRepository.findAllById(todayCardMemberIds)).thenReturn(List.of(activeMember));

        // when
        try (MockedStatic<MemberIntroduction> memberIntroductionMockedStatic = mockStatic(MemberIntroduction.class)) {
            final MemberIntroduction memberIntroduction = mock(MemberIntroduction.class);
            memberIntroductionMockedStatic.when(
                    () -> MemberIntroduction.of(memberId, activeMember.getId(), IntroductionType.TODAY_CARD))
                .thenReturn(memberIntroduction);
            todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);

            // then
            verify(memberIntroductionCommandRepository, times(1)).saveAll(List.of(memberIntroduction));
        }
    }
}