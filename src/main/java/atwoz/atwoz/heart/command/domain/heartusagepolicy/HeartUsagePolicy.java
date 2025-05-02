package atwoz.atwoz.heart.command.domain.heartusagepolicy;


import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.heart.command.domain.hearttransaction.exception.InvalidHeartTransactionTypeException;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "heart_usage_policies")
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

    private HeartUsagePolicy(TransactionType transactionType, Gender gender, HeartPriceAmount heartPriceAmount) {
        setTransactionType(transactionType);
        setGender(gender);
        setHeartPriceAmount(heartPriceAmount);
    }

    public static HeartUsagePolicy of(TransactionType usage, Gender gender, HeartPriceAmount heartPriceAmount) {
        return new HeartUsagePolicy(usage, gender, heartPriceAmount);
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    private void setTransactionType(@NonNull TransactionType transactionType) {
        if (!transactionType.isUsingType()) {
            throw new InvalidHeartTransactionTypeException(
                "TransactionType이 UsingType이 아닙니다. transactionType: " + transactionType);
        }
        this.transactionType = transactionType;
    }

    public Long getAmount(boolean isVip) {
        if (isVip) {
            return 0L;
        }
        return this.heartPriceAmount.getAmount();
    }

    public Long getPrice() {
        return this.heartPriceAmount.getPrice();
    }

    private void setGender(@NonNull Gender gender) {
        this.gender = gender;
    }

    private void setHeartPriceAmount(@NonNull HeartPriceAmount heartPriceAmount) {
        this.heartPriceAmount = heartPriceAmount;
    }
}
