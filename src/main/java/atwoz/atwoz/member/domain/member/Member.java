package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.member.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.domain.member.vo.Nickname;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;

    @Embedded
    private MemberProfile memberProfile;

    private boolean isVip;

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
        return this.memberProfile.getGender();
    }

    public MemberProfile getProfile() {
        return this.memberProfile;
    }

    public void updateMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public boolean isProfileSettingNeeded() {
        if (memberProfile == null) return true;
        return this.memberProfile.isProfileSettingNeeded();
    }
}
