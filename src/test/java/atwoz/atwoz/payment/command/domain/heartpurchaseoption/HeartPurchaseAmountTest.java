package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import atwoz.atwoz.payment.command.domain.heartpurchaseoption.exception.InvalidHeartPurchaseAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartPurchaseAmountTest {

    @Test
    @DisplayName("amount 값이 1보다 작으면 예외가 발생한다.")
    void throwsExceptionWhenAmountIsLessThanOne() {
        // given
        Long amount = 0L;

        // when, then
        assertThatThrownBy(() -> HeartPurchaseAmount.from(amount))
            .isInstanceOf(InvalidHeartPurchaseAmountException.class);
    }

    @Test
    @DisplayName("amount 값이 null이면 예외가 발생한다.")
    void throwsExceptionWhenAmountIsNull() {
        // given
        Long amount = null;

        // when, then
        assertThatThrownBy(() -> HeartPurchaseAmount.from(amount))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("amount 값이 0보다 크면 객체가 생성된다.")
    void createObjectWhenAmountIsGreaterThanZero() {
        // given
        Long amount = 1L;

        // when
        HeartPurchaseAmount heartPurchaseAmount = HeartPurchaseAmount.from(amount);

        // then
        assertThat(heartPurchaseAmount.getAmount()).isEqualTo(amount);
    }
}