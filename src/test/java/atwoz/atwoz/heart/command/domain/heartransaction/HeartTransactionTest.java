package atwoz.atwoz.heart.command.domain.heartransaction;

import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.exception.InvalidHeartAmountException;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartTransactionTest {

    @Nested
    @DisplayName("of 메서드 테스트")
    class OfMethodTest {
        @ParameterizedTest
        @ValueSource(strings = {"memberId is null", "transactionType is null", "heartAmount is null", "heartBalance is null"})
        @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
        void ofMethodWithNullFieldThrowsException(String fieldName) {
            // given
            Long memberId = fieldName.equals("memberId is null") ? null : 1L;
            TransactionType transactionType =
                fieldName.equals("transactionType is null") ? null : TransactionType.MISSION;
            HeartAmount heartAmount = fieldName.equals("heartAmount is null") ? null : HeartAmount.from(10L);
            HeartBalance heartBalance = fieldName.equals("heartBalance is null") ? null : HeartBalance.init();

            // when & then
            assertThatThrownBy(() -> HeartTransaction.of(memberId, transactionType, heartAmount, heartBalance))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("사용 타입인데 heartAmount가 사용량이면 성공")
        void shouldCreateHeartTransactionSuccessWhenTypeIsUsingTypeAndAmountIsUsingType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MESSAGE;
            HeartAmount heartAmount = HeartAmount.from(-10L);
            HeartBalance heartBalance = HeartBalance.init();

            // when
            HeartTransaction heartTransaction = HeartTransaction.of(memberId, transactionType, heartAmount,
                heartBalance);

            // then
            assertThat(heartTransaction).isNotNull();
        }

        @Test
        @DisplayName("획득 타입인데 heartAmount가 획득량이면 성공")
        void shouldCreateHeartTransactionSuccessWhenTypeIsGainingTypeAndAmountIsGainingType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MISSION;
            HeartAmount heartAmount = HeartAmount.from(10L);
            HeartBalance heartBalance = HeartBalance.init();

            // when
            HeartTransaction heartTransaction = HeartTransaction.of(memberId, transactionType, heartAmount,
                heartBalance);

            // then
            assertThat(heartTransaction).isNotNull();
        }

        @Test
        @DisplayName("사용 타입인데 heartAmount가 사용량이 아니면 실패")
        void shouldCreateHeartTransactionFailWhenTypeIsUsingTypeButAmountIsNotUsingType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MESSAGE;
            HeartAmount heartAmount = HeartAmount.from(10L);
            HeartBalance heartBalance = HeartBalance.init();

            // when
            // then
            assertThatThrownBy(() -> HeartTransaction.of(memberId, transactionType, heartAmount, heartBalance))
                .isInstanceOf(InvalidHeartAmountException.class);
        }

        @Test
        @DisplayName("획득 타입인데 heartAmount가 획득량이 아니면 실패")
        void shouldCreateHeartTransactionFailWhenTypeIsGainingTypeButAmountIsNotGainingType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MISSION;
            HeartAmount heartAmount = HeartAmount.from(-10L);
            HeartBalance heartBalance = HeartBalance.init();

            // when
            // then
            assertThatThrownBy(() -> HeartTransaction.of(memberId, transactionType, heartAmount, heartBalance))
                .isInstanceOf(InvalidHeartAmountException.class);
        }
    }
}
