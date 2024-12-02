package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UsageTypeMapperTest {

    @Test
    @DisplayName("UsageType이 null이면 IllegalArgumentException 발생")
    void toTransactionTypeTestWhenUsageTypeIsNull() {
        // given
        UsageType usageType = null;
        // when & then
        assertThatThrownBy(() -> UsageTypeMapper.toTransactionType(usageType))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("UsageType이 MESSAGE이면 TransactionType.MESSAGE 반환")
    void toTransactionTypeTestWhenUsageTypeIsMessage() {
        // given
        UsageType usageType = UsageType.MESSAGE;
        // when
        TransactionType transactionType = UsageTypeMapper.toTransactionType(usageType);
        // then
        assertThat(transactionType).isEqualTo(TransactionType.MESSAGE);
    }

    @Test
    @DisplayName("UsageType이 DATING_EXAM이면 TransactionType.DATING_EXAM 반환")
    void toTransactionTypeTestWhenUsageTypeIsDatingExam() {
        // given
        UsageType usageType = UsageType.DATING_EXAM;
        // when
        TransactionType transactionType = UsageTypeMapper.toTransactionType(usageType);
        // then
        assertThat(transactionType).isEqualTo(TransactionType.DATING_EXAM);
    }

    @Test
    @DisplayName("UsageType이 PROFILE_EXCHANGE이면 TransactionType.PROFILE_EXCHANGE 반환")
    void toTransactionTypeTestWhenUsageTypeIsProfileExchange() {
        // given
        UsageType usageType = UsageType.PROFILE_EXCHANGE;
        // when
        TransactionType transactionType = UsageTypeMapper.toTransactionType(usageType);
        // then
        assertThat(transactionType).isEqualTo(TransactionType.PROFILE_EXCHANGE);
    }

    @Test
    @DisplayName("UsageType이 SELF_INTRODUCTION이면 TransactionType.SELF_INTRODUCTION 반환")
    void toTransactionTypeTestWhenUsageTypeIsSelfIntroduction() {
        // given
        UsageType usageType = UsageType.SELF_INTRODUCTION;
        // when
        TransactionType transactionType = UsageTypeMapper.toTransactionType(usageType);
        // then
        assertThat(transactionType).isEqualTo(TransactionType.SELF_INTRODUCTION);
    }
}