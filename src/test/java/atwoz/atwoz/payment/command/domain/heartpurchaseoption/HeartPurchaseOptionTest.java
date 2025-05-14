package atwoz.atwoz.payment.command.domain.heartpurchaseoption;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.exception.InvalidHeartPurchaseOptionException;
import atwoz.atwoz.payment.command.domain.order.event.HeartPurchasedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class HeartPurchaseOptionTest {
    @ParameterizedTest
    @ValueSource(strings = {"amount is null", "price is null", "name is null", "productId is null"})
    @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
    void throwsExceptionWhenFieldValueIsNull(String fieldName) {
        // given
        HeartPurchaseAmount amount = fieldName.equals("amount is null") ? null : HeartPurchaseAmount.from(10L);
        Price price = fieldName.equals("price is null") ? null : Price.from(1000L);
        String productId = fieldName.equals("productId is null") ? null : "productId";
        String name = fieldName.equals("name is null") ? null : "name";

        // when & then
        assertThatThrownBy(() -> HeartPurchaseOption.of(amount, price, productId, name))
            .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"name is blank", "productId is blank"})
    @DisplayName("name, productId 이 blank 이면 예외가 발생한다.")
    void throwsExceptionWhenNameOrProductIdIsBlank(String fieldName) {
        // given
        HeartPurchaseAmount amount = HeartPurchaseAmount.from(10L);
        Price price = Price.from(1000L);
        String productId = fieldName.equals("productId is blank") ? " " : "productId";
        String name = fieldName.equals("name is blank") ? " " : "name";

        // when, then
        assertThatThrownBy(() -> HeartPurchaseOption.of(amount, price, productId, name))
            .isInstanceOf(InvalidHeartPurchaseOptionException.class);
    }

    @Test
    @DisplayName("amount, price, name 값이 주어지면 객체가 생성된다.")
    void createObjectWhenAmountPriceNameAreGiven() {
        // given
        HeartPurchaseAmount amount = HeartPurchaseAmount.from(10L);
        Price price = Price.from(1000L);
        String productId = "productId";
        String name = "name";

        // when
        HeartPurchaseOption heartPurchaseOption = HeartPurchaseOption.of(amount, price, productId, name);

        // then
        assertThat(heartPurchaseOption).isNotNull();
    }

    @Test
    @DisplayName("purchase를 호출하면 이벤트를 호출한다")
    void publishEventWhenCallPurchaseMethod() {
        // given
        Long amount = 10L;
        HeartPurchaseAmount heartPurchaseAmount = HeartPurchaseAmount.from(amount);
        Price price = Price.from(1000L);
        String productId = "productId";
        String name = "name";
        HeartPurchaseOption heartPurchaseOption = HeartPurchaseOption.of(heartPurchaseAmount, price, productId, name);
        Long memberId = 1L;
        Integer quantity = 5;
        Long expectedAmount = amount * quantity;

        try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
            // When
            heartPurchaseOption.purchase(memberId, quantity);

            // Then
            eventsMockedStatic.verify(() ->
                Events.raise(argThat((HeartPurchasedEvent event) ->
                    event.getMemberId().equals(memberId) &&
                        event.getAmount().equals(expectedAmount)
                )), times(1));
        }
    }
}