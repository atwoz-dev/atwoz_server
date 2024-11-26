package awtoz.awtoz.hearttransaction.domain;

import awtoz.awtoz.hearttransaction.domain.vo.TransactionType;
import awtoz.awtoz.hearttransaction.exception.InvalidHeartTransactionTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeartTransactionTest {

    @Nested
    @DisplayName("useHeart 메서드 테스트")
    class UseHeartTest {
        @Test
        @DisplayName("정상값으로 하트 사용하는 HeartTransaction 생성 성공")
        void useHeartTestWithValidParams() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MESSAGE;
            Long amount = -1L;
            Long balance = 1L;
            // when
            HeartTransaction heartTransaction = HeartTransaction.useHeart(memberId, transactionType, amount, balance);
            // then
            assertThat(heartTransaction).isNotNull();
        }

        @Test
        @DisplayName("잘못된 트랜잭션 타입으로 하트 사용하는 HeartTransaction 생성 실패")
        void useHeartTestWithInvalidTransactionType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MISSION;
            Long amount = -1L;
            Long balance = 1L;
            // when
            // then
            assertThatThrownBy(() -> HeartTransaction.useHeart(memberId, transactionType, amount, balance))
                    .isInstanceOf(InvalidHeartTransactionTypeException.class);
        }
    }

    @Nested
    @DisplayName("gainHeart 메서드 테스트")
    class GainHeartTest {
        @Test
        @DisplayName("정상값으로 하트 얻는 HeartTransaction 생성 성공")
        void gainHeartTestWithValidParams() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MISSION;
            Long amount = 1L;
            Long balance = 1L;
            // when
            HeartTransaction heartTransaction = HeartTransaction.gainHeart(memberId, transactionType, amount, balance);
            // then
            assertThat(heartTransaction).isNotNull();
        }

        @Test
        @DisplayName("잘못된 트랜잭션 타입으로 하트 얻는 HeartTransaction 생성 실패")
        void gainHeartTestWithInvalidTransactionType() {
            // given
            Long memberId = 1L;
            TransactionType transactionType = TransactionType.MESSAGE;
            Long amount = 1L;
            Long balance = 1L;
            // when
            // then
            assertThatThrownBy(() -> HeartTransaction.gainHeart(memberId, transactionType, amount, balance))
                    .isInstanceOf(InvalidHeartTransactionTypeException.class);
        }
    }
}