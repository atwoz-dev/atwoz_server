package atwoz.atwoz.heartpurchaseoption.domain;

import atwoz.atwoz.heartpurchaseoption.exception.InvalidHeartPurchaseOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartPurchaseOptionTest {
    @ParameterizedTest
    @ValueSource(strings = {"amount is null", "price is null", "name is null"})
    @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
    void throwsExceptionWhenFieldValueIsNull(String fieldName) {
        // given
        HeartPurchaseAmount amount = fieldName.equals("amount is null") ? null : HeartPurchaseAmount.from(10L);
        Price price = fieldName.equals("price is null") ? null : Price.from(1000L);
        String name = fieldName.equals("name is null") ? null : "name";

        // when & then
        assertThatThrownBy(() -> HeartPurchaseOption.of(amount, price, name))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("name 값이 빈 문자열이면 예외가 발생한다.")
    void throwsExceptionWhenNameIsEmpty() {
        // given
        HeartPurchaseAmount amount = HeartPurchaseAmount.from(10L);
        Price price = Price.from(1000L);
        String name = "";

        // when, then
        assertThatThrownBy(() -> HeartPurchaseOption.of(amount, price, name))
                .isInstanceOf(InvalidHeartPurchaseOptionException.class);
    }

    @Test
    @DisplayName("name 값이 공백이면 예외가 발생한다.")
    void throwsExceptionWhenNameIsBlank() {
        // given
        HeartPurchaseAmount amount = HeartPurchaseAmount.from(10L);
        Price price = Price.from(1000L);
        String name = "   ";

        // when, then
        assertThatThrownBy(() -> HeartPurchaseOption.of(amount, price, name))
                .isInstanceOf(InvalidHeartPurchaseOptionException.class);
    }

    @Test
    @DisplayName("amount, price, name 값이 주어지면 객체가 생성된다.")
    void createObjectWhenAmountPriceNameAreGiven() {
        // given
        HeartPurchaseAmount amount = HeartPurchaseAmount.from(10L);
        Price price = Price.from(1000L);
        String name = "name";

        // when
        HeartPurchaseOption heartPurchaseOption = HeartPurchaseOption.of(amount, price, name);

        // then
        assertThat(heartPurchaseOption).isNotNull();
    }
}