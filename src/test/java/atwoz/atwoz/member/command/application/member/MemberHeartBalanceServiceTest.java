package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberHeartBalanceServiceTest {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @InjectMocks
    private MemberHeartBalanceService memberHeartBalanceService;

    @Nested
    @DisplayName("구매한 하트를 지급할 때")
    class GrantPurchasedHeartsTests {
        @Test
        @DisplayName("멤버가 존재하지 않으면 예외를 던집니다.")
        void shouldThrowExceptionWhenMemberNotFound() {
            // given
            Long memberId = 1L;
            when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberHeartBalanceService.grantPurchasedHearts(memberId, 100L))
                .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("멤버가 존재하면 구매한 하트를 지급합니다.")
        void shouldGrantPurchasedHeartsWhenMemberExists() {
            // given
            Long memberId = 1L;
            Member member = mock(Member.class);
            when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));
            Long amount = 100L;
            HeartAmount heartAmount = HeartAmount.from(amount);

            // when
            memberHeartBalanceService.grantPurchasedHearts(memberId, amount);

            // then
            verify(member).gainPurchaseHeart(heartAmount);
        }
    }

    @Nested
    @DisplayName("미션 하트를 지급할 때")
    class GrantMissionHeartsTests {
        @Test
        @DisplayName("멤버가 존재하지 않으면 예외를 던집니다.")
        void shouldThrowExceptionWhenMemberNotFound() {
            // given
            Long memberId = 1L;
            when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberHeartBalanceService.grantMissionHearts(memberId, 50L, "Daily Login"))
                .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("멤버가 존재하면 미션 하트를 지급합니다.")
        void shouldGrantMissionHeartsWhenMemberExists() {
            // given
            Long memberId = 1L;
            Member member = mock(Member.class);
            when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));
            Long amount = 50L;
            String actionType = "Daily Login";
            HeartAmount heartAmount = HeartAmount.from(amount);

            // when
            memberHeartBalanceService.grantMissionHearts(memberId, amount, actionType);

            // then
            verify(member).gainMissionHeart(heartAmount, actionType);
        }
    }
}