package atwoz.atwoz.order.domain;

import atwoz.atwoz.order.exception.InvalidOrderStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    @Nested
    @DisplayName("Order 객체 생성 테스트")
    class CreateOrderTest {
        @ParameterizedTest
        @ValueSource(strings = {"memberId is null", "heartPurchaseOptionId is null", "paymentMethod is null"})
        @DisplayName("Order의 파라미터가 Null이면 NullPointerException 발생")
        void throwsNullPointerExceptionWhenParameterIsNull(String condition) {
            // given
            Long memberId = condition.equals("memberId is null") ? null : 1L;
            Long heartPurchaseOptionId = condition.equals("heartPurchaseOptionId is null") ? null : 1L;
            PaymentMethod paymentMethod = condition.equals("paymentMethod is null") ? null : PaymentMethod.GOOGLE_PLAY;
            // when & then
            assertThatThrownBy(() -> Order.of(memberId, heartPurchaseOptionId, paymentMethod))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Order의 파라미터가 Null이 아니면 Order 객체 REQUESTED 상태로 생성")
        void createOrderSuccessWhenParameterIsNotNull() {
            // given
            Long memberId = 1L;
            Long heartPurchaseOptionId = 1L;
            PaymentMethod paymentMethod = PaymentMethod.GOOGLE_PLAY;
            // when
            Order order = Order.of(memberId, heartPurchaseOptionId, paymentMethod);
            // then
            assertThat(order).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REQUESTED);
        }
    }

    @Nested
    @DisplayName("Order 객체의 상태가 REQUESTED일 때")
    class OrderStatusRequested {
        Order order;

        @BeforeEach
        void setUp() {
            // given
            order = Order.of(1L, 1L, PaymentMethod.GOOGLE_PLAY);
        }

        @Test
        @DisplayName("pay() 메서드 호출 시 PAID 상태로 변경")
        void changeStatusToPaidWhenPayMethodIsCalled() {
            // when
            order.pay();
            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        @DisplayName("complete() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCompleteMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::complete)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("refund() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenRefundMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::refund)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("cancel() 메서드 호출 시 CANCELED 상태로 변경")
        void changeStatusToCanceledWhenCancelMethodIsCalled() {
            // when
            order.cancel();
            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }
    }

    @Nested
    @DisplayName("Order 객체의 상태가 PAID일 때")
    class OrderStatusPaid {
        Order order;

        @BeforeEach
        void setUp() {
            // given
            order = Order.of(1L, 1L, PaymentMethod.GOOGLE_PLAY);
            order.pay();
        }

        @Test
        @DisplayName("complete() 메서드 호출 시 COMPLETED 상태로 변경")
        void changeStatusToCompletedWhenCompleteMethodIsCalled() {
            // when
            order.complete();
            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("pay() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenPayMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::pay)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("refund() 메서드 호출 시 REFUNDED 상태로 변경")
        void changeStatusToRefundedWhenRefundMethodIsCalled() {
            // when
            order.refund();
            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        }

        @Test
        @DisplayName("cancel() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCancelMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }
    }

    @Nested
    @DisplayName("Order 객체의 상태가 COMPLETED일 때")
    class OrderStatusCompleted {
        Order order;

        @BeforeEach
        void setUp() {
            // given
            order = Order.of(1L, 1L, PaymentMethod.GOOGLE_PLAY);
            order.pay();
            order.complete();
        }

        @Test
        @DisplayName("refund() 메서드 호출 시 REFUNDED 상태로 변경")
        void changeStatusToRefundedWhenRefundMethodIsCalled() {
            // when
            order.refund();
            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        }

        @Test
        @DisplayName("pay() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenPayMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::pay)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("complete() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCompleteMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::complete)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("cancel() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCancelMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }
    }

    @Nested
    @DisplayName("Order 객체의 상태가 REFUNDED일 때")
    class OrderStatusRefunded {
        Order order;

        @BeforeEach
        void setUp() {
            // given
            order = Order.of(1L, 1L, PaymentMethod.GOOGLE_PLAY);
            order.pay();
            order.complete();
            order.refund();
        }

        @Test
        @DisplayName("pay() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenPayMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::pay)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("complete() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCompleteMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::complete)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("refund() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenRefundMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::refund)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("cancel() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCancelMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }
    }

    @Nested
    @DisplayName("Order 객체의 상태가 CANCELED일 때")
    class OrderStatusCanceled {
        Order order;

        @BeforeEach
        void setUp() {
            // given
            order = Order.of(1L, 1L, PaymentMethod.GOOGLE_PLAY);
            order.cancel();
        }

        @Test
        @DisplayName("pay() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenPayMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::pay)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("complete() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCompleteMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::complete)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("refund() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenRefundMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::refund)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }

        @Test
        @DisplayName("cancel() 메서드 호출 시 InvalidOrderStatusException 발생")
        void throwsInvalidOrderStatusExceptionWhenCancelMethodIsCalled() {
            // when & then
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStatusException.class);
        }
    }
}