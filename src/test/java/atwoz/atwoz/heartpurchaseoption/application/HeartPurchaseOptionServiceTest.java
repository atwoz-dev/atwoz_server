package atwoz.atwoz.heartpurchaseoption.application;

import atwoz.atwoz.heartpurchaseoption.application.exception.HeartPurchaseOptionNotFoundException;
import atwoz.atwoz.heartpurchaseoption.application.exception.MemberNotFoundException;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOption;
import atwoz.atwoz.heartpurchaseoption.domain.HeartPurchaseOptionRepository;
import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class HeartPurchaseOptionServiceTest {

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Mock
    private HeartPurchaseOptionRepository heartPurchaseOptionRepository;

    @Mock
    private HeartTransactionRepository heartTransactionRepository;

    @InjectMocks
    private HeartPurchaseOptionService heartPurchaseOptionService;

    @Test
    @DisplayName("멤버가 존재하지 않을 때 MemberNotFoundException 발생")
    public void throwMemberNotFoundExceptionWhenMemberIsNotExists() {
        // given
        Long memberId = 1L;
        String productId = "productId";
        int quantity = 1;

        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> heartPurchaseOptionService.grantPurchasedHearts(productId, quantity, memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("하트 구매 옵션이 존재하지 않을 때 HeartPurchaseOptionNotFoundException 발생")
    public void throwHeartPurchaseOptionNotFoundExceptionWhenHeartPurchaseOptionIsNotExists() {
        // given
        Long memberId = 1L;
        String productId = "productId";
        int quantity = 1;

        Member member = mock(Member.class);
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(heartPurchaseOptionRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> heartPurchaseOptionService.grantPurchasedHearts(productId, quantity, memberId))
                .isInstanceOf(HeartPurchaseOptionNotFoundException.class);
    }

    @Test
    @DisplayName("멤버와 하트 구매 옵션이 모두 존재하면 하트 지급 성공")
    public void successWhenMemberIsExistsAndHeartPurchaseOptionIsExists() {
        // given
        Long memberId = 1L;
        String productId = "productId";
        int quantity = 1;

        Member member = mock(Member.class);
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));

        HeartPurchaseOption purchaseOption = mock(HeartPurchaseOption.class);
        when(purchaseOption.getHeartAmount()).thenReturn(100L);
        when(heartPurchaseOptionRepository.findByProductId(productId)).thenReturn(Optional.of(purchaseOption));

        HeartBalance heartBalance = mock(HeartBalance.class);
        when(member.getHeartBalance()).thenReturn(heartBalance);

        // when
        heartPurchaseOptionService.grantPurchasedHearts(productId, quantity, memberId);

        // then
        verify(member).gainPurchaseHeart(HeartAmount.from(100L));
        verify(heartTransactionRepository).save(any(HeartTransaction.class));
    }
}
