package awtoz.awtoz.hearttransaction.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class HeartAmountTest {

    @Nested
    @DisplayName("from 메서드 테스트")
    class FromTest {
        @Test
        @DisplayName("amount값 0으로 from 메서드 성공")
        void fromTestWithZeroValue() {
            // given
            Long amount = 0L;
            // when
            HeartAmount heartAmount = HeartAmount.from(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("amount값 음수로 from 메서드 성공")
        void fromTestWithNegativeValue() {
            // given
            Long amount = -1L;
            // when
            HeartAmount heartAmount = HeartAmount.from(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("amount값 양수로 from 메서드 성공")
        void fromTestWithPositiveValue() {
            // given
            Long amount = 1L;
            // when
            HeartAmount heartAmount = HeartAmount.from(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }
    }

    @Nested
    @DisplayName("isUsingAmount 메서드 테스트")
    class isUsingAmountTest {

        @Test
        @DisplayName("amount값 양수로 isUsingAmount 메서드 False 리턴")
        void isUsingAmountTestWithPositiveValue() {
            // given
            Long amount = 1L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isUsingAmount();
            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("amount값 0으로 isUsingAmount 메서드 True 리턴")
        void isUsingAmountTestWithZeroValue() {
            // given
            Long amount = 0L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isUsingAmount();
            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("amount값 음수로 isUsingAmount 메서드 True 리턴")
        void isUsingAmountTestWithNegativeValue() {
            // given
            Long amount = -1L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isUsingAmount();
            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("isGainingAmount 메서드 테스트")
    class isGainingAmountTest {

        @Test
        @DisplayName("amount값 양수로 isGainingAmount 메서드 True 리턴")
        void isGainingAmountTestWithPositiveValue() {
            // given
            Long amount = 1L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isGainingAmount();
            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("amount값 0으로 isGainingAmount 메서드 False 리턴")
        void isGainingAmountTestWithZeroValue() {
            // given
            Long amount = 0L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isGainingAmount();
            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("amount값 음수로 isGainingAmount 메서드 False 리턴")
        void isGainingAmountTestWithNegativeValue() {
            // given
            Long amount = -1L;
            HeartAmount heartAmount = HeartAmount.from(amount);
            // when
            boolean result = heartAmount.isGainingAmount();
            // then
            assertThat(result).isFalse();
        }

    }
}