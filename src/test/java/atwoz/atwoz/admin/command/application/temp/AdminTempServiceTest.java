package atwoz.atwoz.admin.command.application.temp;

import atwoz.atwoz.admin.presentation.temp.dto.GrantMissionHeartRequest;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransactionCommandRepository;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
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
class AdminTempServiceTest {

    @InjectMocks
    private AdminTempService adminTempService;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private HeartTransactionCommandRepository heartTransactionCommandRepository;

    @Test
    @DisplayName("회원이 존재하지 않으면 예외를 던진다.")
    void grantMissionHeart() {
        // given
        GrantMissionHeartRequest request = new GrantMissionHeartRequest(1L, 100L);
        when(memberCommandRepository.findById(request.memberId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminTempService.grantMissionHeart(request))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("회원이 존재하면 미션 하트를 부여한다.")
    void grantMissionHeart_Success() {
        // given
        GrantMissionHeartRequest request = new GrantMissionHeartRequest(1L, 100L);
        Member member = mock(Member.class);
        when(memberCommandRepository.findById(request.memberId())).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(request.memberId());
        HeartBalance heartBalance = mock(HeartBalance.class);
        when(member.getHeartBalance()).thenReturn(heartBalance);

        // when
        try (MockedStatic<HeartAmount> heartAmountMockedStatic = mockStatic(HeartAmount.class);
            MockedStatic<HeartTransaction> heartTransactionMockedStatic = mockStatic(HeartTransaction.class)
        ) {
            HeartAmount heartAmount = mock(HeartAmount.class);
            heartAmountMockedStatic.when(() -> HeartAmount.from(request.heartAmount())).thenReturn(heartAmount);

            HeartTransaction heartTransaction = mock(HeartTransaction.class);
            heartTransactionMockedStatic.when(() -> HeartTransaction.of(
                request.memberId(),
                TransactionType.MISSION,
                heartAmount,
                heartBalance
            )).thenReturn(heartTransaction);

            adminTempService.grantMissionHeart(request);

            // then
            verify(member).gainMissionHeart(heartAmount);
            verify(heartTransactionCommandRepository).save(heartTransaction);
        }
    }
}