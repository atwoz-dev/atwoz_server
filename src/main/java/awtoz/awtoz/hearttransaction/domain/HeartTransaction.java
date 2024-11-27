package awtoz.awtoz.hearttransaction.domain;

import awtoz.awtoz.hearttransaction.domain.vo.HeartAmount;
import awtoz.awtoz.hearttransaction.domain.vo.HeartBalance;
import awtoz.awtoz.hearttransaction.domain.vo.TransactionType;
import awtoz.awtoz.hearttransaction.exception.InvalidHeartAmountException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartTransaction {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private TransactionType transactionType;

    @Embedded
    private HeartAmount heartAmount;

    @Embedded
    private HeartBalance heartBalance;

    public static HeartTransaction of(Long memberId, TransactionType transactionType, HeartAmount heartAmount, HeartBalance heartBalance) {
        return new HeartTransaction(memberId, transactionType, heartAmount, heartBalance);
    }

    private HeartTransaction(Long memberId, TransactionType transactionType, HeartAmount heartAmount, HeartBalance heartBalance) {
        validateHeartTransaction(transactionType, heartAmount);
        setMemberId(memberId);
        setTransactionType(transactionType);
        setHeartAmount(heartAmount);
        setHeartBalance(heartBalance);
    }

    private void setMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 null이 될 수 없습니다.");
        }
        this.memberId = memberId;
    }

    private void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("transactionType은 null이 될 수 없습니다.");
        }
        this.transactionType = transactionType;
    }

    private void setHeartAmount(HeartAmount heartAmount) {
        if (heartAmount == null) {
            throw new IllegalArgumentException("heartAmount는 null이 될 수 없습니다.");
        }
        this.heartAmount = heartAmount;
    }

    private void setHeartBalance(HeartBalance heartBalance) {
        if (heartBalance == null) {
            throw new IllegalArgumentException("heartBalance는 null이 될 수 없습니다.");
        }
        this.heartBalance = heartBalance;
    }

    private void validateHeartTransaction(TransactionType transactionType, HeartAmount heartAmount) {
        if (transactionType.isUsingType() && !heartAmount.isUsingAmount()) {
            throw new InvalidHeartAmountException("잘못된 하트 사용량 입니다. transactionType: " + transactionType + ", heartAmount: " + heartAmount);
        }
        if (transactionType.isGainingType() && !heartAmount.isGainingAmount()) {
            throw new InvalidHeartAmountException("잘못된 하트 획득량 입니다. transactionType: " + transactionType + ", heartAmount: " + heartAmount);
        }
    }
}
