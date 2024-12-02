package atwoz.atwoz.heartusagepolicy.domain;


import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
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
    private UsageType usageType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    @Embedded
    private HeartPriceAmount heartPriceAmount;

    public static HeartUsagePolicy of(UsageType usage, Gender gender, HeartPriceAmount heartPriceAmount) {
        return new HeartUsagePolicy(usage, gender, heartPriceAmount);
    }

    private HeartUsagePolicy(UsageType usageType, Gender gender, HeartPriceAmount heartPriceAmount) {
        setUsageType(usageType);
        setGender(gender);
        setHeartPriceAmount(heartPriceAmount);
    }

    public TransactionType getTransactionType() {
        return UsageTypeMapper.toTransactionType(usageType);
    }

    private void setUsageType(UsageType usageType) {
        if (usageType == null) {
            throw new IllegalArgumentException("UsageType는 null이 될 수 없습니다.");
        }
        this.usageType = usageType;
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
