package atwoz.atwoz.heart.command.domain.hearttransaction;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.heart.command.domain.hearttransaction.exception.InvalidHeartAmountException;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "heart_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartTransaction extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Getter
    private Long id;

    @Getter
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private TransactionType transactionType;

    @Embedded
    @Getter
    private HeartAmount heartAmount;

    @Embedded
    @Getter
    private HeartBalance heartBalance;

    private HeartTransaction(Long memberId, TransactionType transactionType, HeartAmount heartAmount,
        HeartBalance heartBalance) {
        setMemberId(memberId);
        setTransactionType(transactionType);
        setHeartAmount(heartAmount);
        setHeartBalance(heartBalance);
        validateHeartTransaction(transactionType, heartAmount);
    }

    public static HeartTransaction of(Long memberId, TransactionType transactionType, HeartAmount heartAmount,
        HeartBalance heartBalance) {
        return new HeartTransaction(memberId, transactionType, heartAmount, heartBalance);
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setTransactionType(@NonNull TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    private void setHeartAmount(@NonNull HeartAmount heartAmount) {
        this.heartAmount = heartAmount;
    }

    private void setHeartBalance(@NonNull HeartBalance heartBalance) {
        this.heartBalance = heartBalance;
    }

    private void validateHeartTransaction(TransactionType transactionType, HeartAmount heartAmount) {
        if (transactionType.isUsingType() && !heartAmount.isUsingAmount()) {
            throw new InvalidHeartAmountException(
                "잘못된 하트 사용량 입니다. transactionType: " + transactionType + ", heartAmount: " + heartAmount);
        }
        if (transactionType.isGainingType() && !heartAmount.isGainingAmount()) {
            throw new InvalidHeartAmountException(
                "잘못된 하트 획득량 입니다. transactionType: " + transactionType + ", heartAmount: " + heartAmount);
        }
    }
}
