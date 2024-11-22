package awtoz.awtoz.heart.domain.vo;

import awtoz.awtoz.heart.exception.InvalidHeartAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class HeartAmountTest {

    @Nested
    @DisplayName("use 메서드 테스트")
    class useTest {
        @Test
        @DisplayName("amount값 0으로 하트 사용하는 HeartAmount 생성 성공")
        void useTestWithZeroValue() {
            // given
            Long amount = 0L;
            // when
            HeartAmount heartAmount = HeartAmount.use(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("amount값 음수로 하트 사용하는 HeartAmount 생성 성공")
        void useTestWithNegativeValue() {
            // given
            Long amount = -1L;
            // when
            HeartAmount heartAmount = HeartAmount.use(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("amount값 양수로 하트 사용하는 HeartAmount 생성시 예외발생")
        void useTestWithPositiveValue() {
            // given
            Long amount = 1L;
            // when
            // then
            assertThatThrownBy(() -> HeartAmount.use(amount))
                    .isInstanceOf(InvalidHeartAmountException.class);
        }
    }

    @Nested
    @DisplayName("gain 메서드 테스트")
    class gainTest {

        @Test
        @DisplayName("amount값 양수로 하트 얻는 HeartAmount 생성 성공")
        void gainTestWithPositiveValue() {
            // given
            Long amount = 1L;
            // when
            HeartAmount heartAmount = HeartAmount.gain(amount);
            // then
            assertThat(heartAmount.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("amount값 0으로 하트 얻는 HeartAmount 생성시 예외발생")
        void gainTestWithZeroValue() {
            // given
            Long amount = 0L;
            // when
            // then
            assertThatThrownBy(() -> HeartAmount.gain(amount))
                    .isInstanceOf(InvalidHeartAmountException.class);
        }

        @Test
        @DisplayName("amount값 음수로 하트 얻는 HeartAmount 생성시 예외발생")
        void gainTestWithNegativeValue() {
            // given
            Long amount = -1L;
            // when
            // then
            assertThatThrownBy(() -> HeartAmount.gain(amount))
                    .isInstanceOf(InvalidHeartAmountException.class);
        }
    }
}