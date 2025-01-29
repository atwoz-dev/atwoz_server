package atwoz.atwoz.heartusagepolicy.application;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.domain.HeartPriceAmount;
import atwoz.atwoz.heartusagepolicy.domain.HeartUsagePolicy;
import atwoz.atwoz.heartusagepolicy.domain.HeartUsagePolicyRepository;
import atwoz.atwoz.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
        Member member = Member.fromPhoneNumber("01012345678");
        Gender gender = Gender.MALE;
        MemberProfile memberProfile = MemberProfile.builder()
                .gender(gender)
                .build();
        setField(member, "profile", memberProfile);
        TransactionType transactionType = TransactionType.MESSAGE;
        when(heartUsagePolicyRepository.findByGenderAndTransactionType(eq(gender), eq(transactionType)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> heartUsageService.useHeart(member, transactionType))
                .isInstanceOf(HeartUsagePolicyNotFoundException.class);

        verify(heartUsagePolicyRepository, atMostOnce()).findByGenderAndTransactionType(eq(gender), eq(transactionType));
        verify(heartTransactionRepository, never()).save(any(HeartTransaction.class));
    }

    @Test
    @DisplayName("VIP 멤버인 경우 하트 사용량 0으로 처리")
    void shouldUseZeroHeartAmountWhenMemberIsVip() {
        // given
        Member member = Member.fromPhoneNumber("01012345678");
        setField(member, "isVip", true);
        Long memberId = 1L;
        setField(member, "id", memberId);
        Gender gender = Gender.MALE;
        MemberProfile memberProfile = MemberProfile.builder()
                .gender(gender)
                .build();
        setField(member, "profile", memberProfile);

        HeartBalance heartBalanceBeforeUsingHeart = HeartBalance.init();
        setField(heartBalanceBeforeUsingHeart, "purchaseHeartBalance", 100L);
        setField(heartBalanceBeforeUsingHeart, "missionHeartBalance", 100L);
        setField(member, "heartBalance", heartBalanceBeforeUsingHeart);
        TransactionType transactionType = TransactionType.MESSAGE;
        HeartPriceAmount heartPriceAmount = HeartPriceAmount.from(10L);
        HeartUsagePolicy heartUsagePolicy = HeartUsagePolicy.of(transactionType, gender, heartPriceAmount);

        when(heartUsagePolicyRepository.findByGenderAndTransactionType(eq(gender), eq(transactionType)))
                .thenReturn(Optional.of(heartUsagePolicy));
        when(heartTransactionRepository.save(any(HeartTransaction.class)))
                .thenAnswer(invocation -> {
                    HeartTransaction heartTransaction = invocation.getArgument(0);
                    setField(heartTransaction, "id", 1L);
                    return heartTransaction;
                });
        HeartAmount expectedHeartAmount = HeartAmount.from(0L);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(member, transactionType);

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
        assertThat(heartTransaction.getHeartBalance()).isEqualTo(heartBalanceBeforeUsingHeart);

        verify(heartUsagePolicyRepository, atMostOnce()).findByGenderAndTransactionType(eq(gender), eq(transactionType));
        verify(heartTransactionRepository, atMostOnce()).save(any(HeartTransaction.class));
    }

    @Test
    @DisplayName("VIP 멤버가 아닌 경우 하트 사용 정책에 따라 하트 사용량 계산")
    void shouldCalculateHeartAmountAccordingToHeartUsagePolicy() {
        // given
        Member member = Member.fromPhoneNumber("01012345678");
        Long memberId = 1L;

        setField(member, "id", memberId);
        Gender gender = Gender.MALE;
        MemberProfile memberProfile = MemberProfile.builder()
                .gender(gender)
                .build();
        setField(member, "profile", memberProfile);
        HeartBalance heartBalanceBeforeUsingHeart = HeartBalance.init();
        setField(heartBalanceBeforeUsingHeart, "purchaseHeartBalance", 100L);
        setField(heartBalanceBeforeUsingHeart, "missionHeartBalance", 100L);
        setField(member, "heartBalance", heartBalanceBeforeUsingHeart);
        TransactionType transactionType = TransactionType.MESSAGE;
        HeartPriceAmount heartPriceAmount = HeartPriceAmount.from(10L);
        HeartUsagePolicy heartUsagePolicy = HeartUsagePolicy.of(transactionType, gender, heartPriceAmount);

        when(heartUsagePolicyRepository.findByGenderAndTransactionType(eq(gender), eq(transactionType)))
                .thenReturn(Optional.of(heartUsagePolicy));
        when(heartTransactionRepository.save(any(HeartTransaction.class)))
                .thenAnswer(invocation -> {
                    HeartTransaction heartTransaction = invocation.getArgument(0);
                    setField(heartTransaction, "id", 1L);
                    return heartTransaction;
                });
        HeartAmount expectedHeartAmount = HeartAmount.from(-10L);
        HeartBalance expectedHeartBalance = heartBalanceBeforeUsingHeart.useHeart(expectedHeartAmount);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(member, transactionType);

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
        assertThat(heartTransaction.getHeartBalance()).isEqualTo(expectedHeartBalance);

        verify(heartUsagePolicyRepository, atMostOnce()).findByGenderAndTransactionType(eq(gender), eq(transactionType));
        verify(heartTransactionRepository, atMostOnce()).save(any(HeartTransaction.class));
    }
}