package awtoz.awtoz.heart.domain;

import awtoz.awtoz.heart.domain.vo.HeartAmount;
import awtoz.awtoz.heart.domain.vo.HeartBalance;
import awtoz.awtoz.heart.domain.vo.TransactionType;
import awtoz.awtoz.heart.exception.InvalidHeartTransactionTypeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HeartTransaction {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Embedded
    private HeartAmount heartAmount;

    @Embedded
    private HeartBalance heartBalance;

    public static HeartTransaction useHeart(Long memberId, TransactionType transactionType, Long amount, Long balance) {
        validateUsingTransaction(transactionType);
        return HeartTransaction.builder()
                .memberId(memberId)
                .transactionType(transactionType)
                .heartAmount(HeartAmount.use(amount))
                .heartBalance(HeartBalance.of(balance))
                .build();
    }

    private static void validateUsingTransaction(TransactionType transactionType) {
        if (!transactionType.isUsingType()) {
            throw new InvalidHeartTransactionTypeException("하트를 사용하는 트랜잭션 타입이 아닙니다.");
        }
    }

    public static HeartTransaction gainHeart(Long memberId, TransactionType transactionType, Long amount, Long balance) {
        validateGainingTransaction(transactionType);
        return HeartTransaction.builder()
                .memberId(memberId)
                .transactionType(transactionType)
                .heartAmount(HeartAmount.gain(amount))
                .heartBalance(HeartBalance.of(balance))
                .build();
    }

    private static void validateGainingTransaction(TransactionType transactionType) {
        if (!transactionType.isGainingType()) {
            throw new InvalidHeartTransactionTypeException("하트를 획득하는 트랜잭션 타입이 아닙니다.");
        }
    }
}
