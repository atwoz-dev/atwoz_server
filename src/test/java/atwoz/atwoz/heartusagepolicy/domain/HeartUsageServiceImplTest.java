package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.member.domain.member.Gender;
import atwoz.atwoz.member.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeartUsageServiceImplTest {
    @InjectMocks
    private HeartUsageServiceImpl heartUsageService;
    @Mock
    private HeartUsagePolicyRepository heartUsagePolicyRepository;
    @Mock
    private HeartTransactionRepository heartTransactionRepository;

    @Test
    @DisplayName("하트 사용 정책이 없는 경우 예외 발생")
    void shouldThrowExceptionWhenHeartUsagePolicyNotFound() {
        // given
        Member member = Member.createFromPhoneNumber("01012345678");
        TransactionType transactionType = TransactionType.MESSAGE;
        when(heartUsagePolicyRepository.findByGenderAndTransactionType(any(), eq(transactionType)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> heartUsageService.useHeart(member, transactionType))
                .isInstanceOf(HeartUsagePolicyNotFoundException.class);
    }

    @Test
    @DisplayName("VIP 멤버인 경우 하트 사용량 0으로 처리")
    void shouldUseZeroHeartAmountWhenMemberIsVip() {
        // given
        Member member = mock(Member.class);
        when(member.isVipMember()).thenReturn(true);
        Long memberId = 1L;
        when(member.getId()).thenReturn(memberId);
        Gender gender = Gender.MALE;
        when(member.getGender()).thenReturn(gender);
        HeartBalance heartBalance = mock(HeartBalance.class);
        when(member.getHeartBalance()).thenReturn(heartBalance);
        HeartUsagePolicy heartUsagePolicy = mock(HeartUsagePolicy.class);
        TransactionType transactionType = TransactionType.MESSAGE;
        when(heartUsagePolicyRepository.findByGenderAndTransactionType(any(), eq(transactionType)))
                .thenReturn(Optional.of(heartUsagePolicy));
        HeartTransaction mockHeartTransaction = mock(HeartTransaction.class);
        when(mockHeartTransaction.getHeartAmount()).thenReturn(HeartAmount.from(0L));
        when(heartTransactionRepository.save(any())).thenReturn(mockHeartTransaction);
        HeartAmount expectedHeartAmount = HeartAmount.from(0L);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(member, transactionType);

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
    }

    @Test
    @DisplayName("VIP 멤버가 아닌 경우 하트 사용 정책에 따라 하트 사용량 계산")
    void shouldCalculateHeartAmountAccordingToHeartUsagePolicy() {
        // given
        Member member = mock(Member.class);
        when(member.isVipMember()).thenReturn(true);
        Long memberId = 1L;
        when(member.getId()).thenReturn(memberId);
        Gender gender = Gender.MALE;
        when(member.getGender()).thenReturn(gender);
        HeartBalance heartBalance = mock(HeartBalance.class);
        when(member.getHeartBalance()).thenReturn(heartBalance);
        HeartUsagePolicy heartUsagePolicy = mock(HeartUsagePolicy.class);
        TransactionType transactionType = TransactionType.MESSAGE;
        when(heartUsagePolicyRepository.findByGenderAndTransactionType(any(), eq(transactionType)))
                .thenReturn(Optional.of(heartUsagePolicy));
        HeartTransaction mockHeartTransaction = mock(HeartTransaction.class);
        when(mockHeartTransaction.getHeartAmount()).thenReturn(HeartAmount.from(-10L));
        when(heartTransactionRepository.save(any())).thenReturn(mockHeartTransaction);
        HeartAmount expectedHeartAmount = HeartAmount.from(-10L);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(member, transactionType);

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
    }
}