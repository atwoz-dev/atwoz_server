package atwoz.atwoz.heartusagepolicy.domain;


import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.hearttransaction.exception.InvalidHeartTransactionTypeException;
import atwoz.atwoz.member.domain.member.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartUsagePolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    @Embedded
    private HeartPriceAmount heartPriceAmount;

    public static HeartUsagePolicy of(TransactionType usage, Gender gender, HeartPriceAmount heartPriceAmount) {
        return new HeartUsagePolicy(usage, gender, heartPriceAmount);
    }

    private HeartUsagePolicy(TransactionType transactionType, Gender gender, HeartPriceAmount heartPriceAmount) {
        setTransactionType(transactionType);
        setGender(gender);
        setHeartPriceAmount(heartPriceAmount);
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public Long getAmount() {
        return this.heartPriceAmount.getAmount();
    }

    public Long getPrice() {
        return this.heartPriceAmount.getPrice();
    }

    private void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("TransactionType은 null이 될 수 없습니다.");
        }
        if (!transactionType.isUsingType()) {
            throw new InvalidHeartTransactionTypeException("TransactionType이 UsingType이 아닙니다. transactionType: " + transactionType);
        }
        this.transactionType = transactionType;
    }

    private void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender는 null이 될 수 없습니다.");
        }
        this.gender = gender;
    }

    private void setHeartPriceAmount(HeartPriceAmount heartPriceAmount) {
        if (heartPriceAmount == null) {
            throw new IllegalArgumentException("HeartItemPrice는 null이 될 수 없습니다.");
        }
        this.heartPriceAmount = heartPriceAmount;
    }
}
