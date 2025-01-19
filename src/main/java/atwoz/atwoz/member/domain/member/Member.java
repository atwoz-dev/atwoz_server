package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    private String phoneNumber;

    @Embedded
    @Getter
    private MemberProfile profile;

    @Getter
    private boolean isVip;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ActivityStatus activityStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private MemberScreeningStatus screeningStatus;

    @Embedded
    @Getter
    private HeartBalance heartBalance;

    public static Member fromPhoneNumber(String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .activityStatus(ActivityStatus.ACTIVE)
                .heartBalance(HeartBalance.init())
                .isVip(false)
                .build();
    }

    public void updateProfile(@NonNull MemberProfile profile) {
        this.profile = profile;
    }

    public boolean isProfileSettingNeeded() {
        if (profile == null) return true;
        return profile.isProfileSettingNeeded();
    }

    public Gender getGender() {
        return profile.getGender();
    }

    public boolean isBanned() {
        return activityStatus == ActivityStatus.BANNED;
    }

    public void useHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.useHeart(heartAmount);
    }

    public void gainPurchaseHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.gainPurchaseHeart(heartAmount);
    }

    public void gainMissionHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.gainMissionHeart(heartAmount);
    }
}
