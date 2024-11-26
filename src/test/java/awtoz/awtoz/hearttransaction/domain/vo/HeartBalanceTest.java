package awtoz.awtoz.hearttransaction.domain.vo;

import awtoz.awtoz.hearttransaction.exception.InvalidHeartBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartBalanceTest {
    @Test
    @DisplayName("balance값 0으로 하트 잔액 생성시 성공")
    void fromTestWithZeroValue() {
        //given
        Long balance = 0L;
        //when
        HeartBalance heartBalance = HeartBalance.from(balance);
        //then
        assertThat(heartBalance.getBalance()).isEqualTo(balance);
    }

    @Test
    @DisplayName("balance값 양수로 하트 잔액 생성시 성공")
    void fromTestWithPositiveValue() {
        //given
        Long balance = 1L;
        //when
        HeartBalance heartBalance = HeartBalance.from(balance);
        //then
        assertThat(heartBalance.getBalance()).isEqualTo(balance);
    }

    @Test
    @DisplayName("balance값 음수로 하트 잔액 생성시 예외발생")
    void fromTestWithNegativeValue() {
        //given
        Long balance = -1L;
        //when
        //then
        assertThatThrownBy(() -> HeartBalance.from(balance))
                .isInstanceOf(InvalidHeartBalanceException.class);
    }
}