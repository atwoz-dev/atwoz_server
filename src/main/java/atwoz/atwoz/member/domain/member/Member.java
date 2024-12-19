package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.member.domain.member.vo.Nickname;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Nickname nickname;

    private String phoneNumber;

    private String region;

    private Integer age;

    private Integer height;

    private boolean isVip;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ActivityStatus activityStatus;

    @Embedded
    private HeartBalance heartBalance;

    public static Member createFromPhoneNumber(String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .activityStatus(ActivityStatus.ACTIVE)
                .heartBalance(HeartBalance.init())
                .isVip(false)
                .build();
    }

    public Long getId() {
        return id;
    }

    public boolean isPermanentStop() {
        return activityStatus == ActivityStatus.PERMANENT_STOP;
    }

    public boolean isProfileSettingNeeded() {
        if (this.nickname == null || gender == null || region == null || age == null || height == null || mbti == null) {
            return true;
        }
        return false;
    }

    public void useHeart(HeartAmount heartAmount) {
        HeartBalance balanceAfterUsingHeart = this.heartBalance.useHeart(heartAmount);
        this.heartBalance = balanceAfterUsingHeart;
    }

    public void gainPurchaseHeart(HeartAmount heartAmount) {
        HeartBalance balanceAfterGainingHeart = this.heartBalance.gainPurchaseHeart(heartAmount);
        this.heartBalance = balanceAfterGainingHeart;
    }

    public void gainMissionHeart(HeartAmount heartAmount) {
        HeartBalance balanceAfterGainingHeart = this.heartBalance.gainMissionHeart(heartAmount);
        this.heartBalance = balanceAfterGainingHeart;
    }

    public HeartBalance getHeartBalance() {
        return this.heartBalance;
    }

    public boolean isVipMember() {
        return this.isVip;
    }

    public Gender getGender() {
        return this.gender;
    }
}
