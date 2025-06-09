package atwoz.atwoz.member.command.application.introduction;


import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotActiveException;
import atwoz.atwoz.member.command.application.introduction.exception.IntroducedMemberNotFoundException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIntroductionAlreadyExistsException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIntroductionServiceTest {

    @InjectMocks
    private MemberIntroductionService memberIntroductionService;

    @Mock
    private MemberIntroductionCommandRepository memberIntroductionCommandRepository;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Test
    @DisplayName("소개받은 멤버가 존재하지 않으면 예외를 던진다.")
    void throwsExceptionWhenIntroducedMemberNotFound() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;

        when(memberCommandRepository.findById(introducedMemberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberIntroductionService.createGradeIntroduction(memberId, introducedMemberId))
            .isInstanceOf(IntroducedMemberNotFoundException.class);
    }

    @Test
    @DisplayName("소개받은 멤버가 Active 상태가 아니라면 예외를 던진다")
    void throwsExceptionWhenIntroducedMemberNotActive() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;

        Member introducedMember = mock(Member.class);
        when(introducedMember.isActive()).thenReturn(false);
        when(memberCommandRepository.findById(introducedMemberId)).thenReturn(Optional.of(introducedMember));

        // when & then
        assertThatThrownBy(() -> memberIntroductionService.createGradeIntroduction(memberId, introducedMemberId))
            .isInstanceOf(IntroducedMemberNotActiveException.class);
    }


    @Test
    @DisplayName("이미 소개받은 멤버라면 예외를 던진다")
    void throwsExceptionWhenMemberAlreadyIntroduced() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;

        Member introducedMember = mock(Member.class);
        when(introducedMember.isActive()).thenReturn(true);
        when(memberCommandRepository.findById(introducedMemberId)).thenReturn(Optional.of(introducedMember));
        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId,
            introducedMemberId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberIntroductionService.createGradeIntroduction(memberId, introducedMemberId))
            .isInstanceOf(MemberIntroductionAlreadyExistsException.class);
    }

    @Test
    @DisplayName("소개받은 멤버가 아니라면 소개를 생성한다")
    void createIntroduction() {
        // given
        long memberId = 1L;
        long introducedMemberId = 2L;

        Member introducedMember = mock(Member.class);
        when(introducedMember.isActive()).thenReturn(true);
        when(memberCommandRepository.findById(introducedMemberId)).thenReturn(Optional.of(introducedMember));
        when(memberIntroductionCommandRepository.existsByMemberIdAndIntroducedMemberId(memberId,
            introducedMemberId)).thenReturn(false);

        // when
        try (MockedStatic<MemberIntroduction> memberIntroductionMock = mockStatic(MemberIntroduction.class)) {
            MemberIntroduction memberIntroduction = mock(MemberIntroduction.class);
            memberIntroductionMock.when(() -> MemberIntroduction.of(memberId, introducedMemberId,
                    IntroductionType.DIAMOND_GRADE))
                .thenReturn(memberIntroduction);
            memberIntroductionService.createGradeIntroduction(memberId, introducedMemberId);

            // then
            verify(memberIntroductionCommandRepository).save(memberIntroduction);
        }
    }
}