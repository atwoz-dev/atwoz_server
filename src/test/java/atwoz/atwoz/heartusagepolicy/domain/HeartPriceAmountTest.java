package atwoz.atwoz.heartusagepolicy.domain;


import atwoz.atwoz.heartusagepolicy.exception.InvalidHeartPriceAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartPriceAmountTest {

    @Test
    @DisplayName("price 값이 0이면 InvalidHeartPriceAmountException 발생")
    void fromTestWhenPriceIsZero() {
        // given
        Long price = 0L;
        // when & then
        assertThatThrownBy(() -> HeartPriceAmount.from(price))
                .isInstanceOf(InvalidHeartPriceAmountException.class);
    }

    @Test
    @DisplayName("price 값이 1 이상이면 HeartPriceAmount 객체 생성 성공")
    void fromTestWhenPriceIsPositive() {
        // given
        Long price = 1L;
        // when
        HeartPriceAmount heartPriceAmount = HeartPriceAmount.from(price);
        // then
        assertThat(heartPriceAmount).isNotNull();
    }
}