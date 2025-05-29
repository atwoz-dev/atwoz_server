package atwoz.atwoz.heart.command.application.heartusagepolicy;

import atwoz.atwoz.heart.command.application.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransactionCommandRepository;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.heart.command.domain.heartusagepolicy.HeartPriceAmount;
import atwoz.atwoz.heart.command.domain.heartusagepolicy.HeartUsagePolicy;
import atwoz.atwoz.heart.command.domain.heartusagepolicy.HeartUsagePolicyCommandRepository;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class HeartUsagePolicyServiceTest {
    @InjectMocks
    private HeartUsagePolicyService heartUsageService;
    @Mock
    private HeartUsagePolicyCommandRepository heartUsagePolicyCommandRepository;
    @Mock
    private HeartTransactionCommandRepository heartTransactionCommandRepository;
    @Mock
    private MemberCommandRepository memberCommandRepository;

    @Test
    @DisplayName("멤버가 존재하지 않는 경우 예외 발생")
    void shouldThrowExceptionWhenMemberNotFound() {
        // given
        Long memberId = 1L;
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());
        TransactionType transactionType = TransactionType.MESSAGE;
        String content = transactionType.getDescription();

        // when & then
        assertThatThrownBy(() -> heartUsageService.useHeart(memberId, transactionType, content))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("하트 사용 정책이 없는 경우 예외 발생")
    void shouldThrowExceptionWhenHeartUsagePolicyNotFound() {
        // given
        Member member = Member.fromPhoneNumber("01012345678");
        Long memberId = 1L;
        setField(member, "id", memberId);
        Gender gender = Gender.MALE;
        MemberProfile memberProfile = MemberProfile.builder()
            .gender(gender)
            .build();
        setField(member, "profile", memberProfile);
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));
        TransactionType transactionType = TransactionType.MESSAGE;
        String content = transactionType.getDescription();
        when(heartUsagePolicyCommandRepository.findByGenderAndTransactionType(gender, transactionType))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> heartUsageService.useHeart(memberId, transactionType, content))
            .isInstanceOf(HeartUsagePolicyNotFoundException.class);

        verify(heartUsagePolicyCommandRepository, atMostOnce()).findByGenderAndTransactionType(gender, transactionType);
        verify(heartTransactionCommandRepository, never()).save(any(HeartTransaction.class));
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
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));

        HeartBalance heartBalanceBeforeUsingHeart = HeartBalance.init();
        setField(heartBalanceBeforeUsingHeart, "purchaseHeartBalance", 100L);
        setField(heartBalanceBeforeUsingHeart, "missionHeartBalance", 100L);
        setField(member, "heartBalance", heartBalanceBeforeUsingHeart);
        TransactionType transactionType = TransactionType.MESSAGE;
        HeartPriceAmount heartPriceAmount = HeartPriceAmount.from(10L);
        HeartUsagePolicy heartUsagePolicy = HeartUsagePolicy.of(transactionType, gender, heartPriceAmount);

        when(heartUsagePolicyCommandRepository.findByGenderAndTransactionType(gender, transactionType))
            .thenReturn(Optional.of(heartUsagePolicy));
        when(heartTransactionCommandRepository.save(any(HeartTransaction.class)))
            .thenAnswer(invocation -> {
                HeartTransaction heartTransaction = invocation.getArgument(0);
                setField(heartTransaction, "id", 1L);
                return heartTransaction;
            });
        HeartAmount expectedHeartAmount = HeartAmount.from(0L);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(memberId, transactionType,
            transactionType.getDescription());

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
        assertThat(heartTransaction.getHeartBalance()).isEqualTo(heartBalanceBeforeUsingHeart);

        verify(heartUsagePolicyCommandRepository, atMostOnce()).findByGenderAndTransactionType(gender, transactionType);
        verify(heartTransactionCommandRepository, atMostOnce()).save(any(HeartTransaction.class));
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
        when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(member));

        HeartBalance heartBalanceBeforeUsingHeart = HeartBalance.init();
        setField(heartBalanceBeforeUsingHeart, "purchaseHeartBalance", 100L);
        setField(heartBalanceBeforeUsingHeart, "missionHeartBalance", 100L);
        setField(member, "heartBalance", heartBalanceBeforeUsingHeart);
        TransactionType transactionType = TransactionType.MESSAGE;
        HeartPriceAmount heartPriceAmount = HeartPriceAmount.from(10L);
        HeartUsagePolicy heartUsagePolicy = HeartUsagePolicy.of(transactionType, gender, heartPriceAmount);

        when(heartUsagePolicyCommandRepository.findByGenderAndTransactionType(gender, transactionType))
            .thenReturn(Optional.of(heartUsagePolicy));
        when(heartTransactionCommandRepository.save(any(HeartTransaction.class)))
            .thenAnswer(invocation -> {
                HeartTransaction heartTransaction = invocation.getArgument(0);
                setField(heartTransaction, "id", 1L);
                return heartTransaction;
            });
        HeartAmount expectedHeartAmount = HeartAmount.from(-10L);
        HeartBalance expectedHeartBalance = heartBalanceBeforeUsingHeart.useHeart(expectedHeartAmount);

        // when
        HeartTransaction heartTransaction = heartUsageService.useHeart(memberId, transactionType,
            transactionType.getDescription());

        // then
        assertThat(heartTransaction.getHeartAmount()).isEqualTo(expectedHeartAmount);
        assertThat(heartTransaction.getHeartBalance()).isEqualTo(expectedHeartBalance);

        verify(heartUsagePolicyCommandRepository, atMostOnce()).findByGenderAndTransactionType(gender, transactionType);
        verify(heartTransactionCommandRepository, atMostOnce()).save(any(HeartTransaction.class));
    }
}