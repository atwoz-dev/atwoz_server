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
    @DisplayName("UsageType의 모든 값이 Mapper에 정의되어 있는지 테스트")
    void toTransactionTypeTestWithAllOfUsageType() {
        // given
        UsageType[] allValues = UsageType.values();
        // when & then
        for (UsageType usageType : allValues) {
            TransactionType transactionType = UsageTypeMapper.toTransactionType(usageType);
            assertThat(transactionType).isNotNull();
        }
    }
}