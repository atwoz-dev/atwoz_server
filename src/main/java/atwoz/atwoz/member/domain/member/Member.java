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

    @Getter
    private String phoneNumber;

    @Embedded
    private KakaoId kakaoId;

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
    private PrimaryContactType primaryContactType;

    @Embedded
    @Getter
    private HeartBalance heartBalance;

    public static Member fromPhoneNumber(@NonNull String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .activityStatus(ActivityStatus.ACTIVE)
                .heartBalance(HeartBalance.init())
                .isVip(false)
                .primaryContactType(PrimaryContactType.PHONE_NUMBER)
                .build();
    }

    public void updateProfile(@NonNull MemberProfile profile) {
        this.profile = profile;
    }

    public boolean isProfileSettingNeeded() {
        if (profile == null) return true;
        return profile.isProfileSettingNeeded();
    }

    public String getKakaoId() {
        return kakaoId.getValue();
    }

    public void changeToDormant() {
        activityStatus = ActivityStatus.DORMANT;
    }

    public void changePrimaryContactTypeToKakao(KakaoId kakaoId) {
        this.kakaoId = kakaoId;
        this.primaryContactType = PrimaryContactType.KAKAO;
    }

    public void changePrimaryContactTypeToPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.primaryContactType = PrimaryContactType.PHONE_NUMBER;
    }

    public boolean isActive() {
        return activityStatus == ActivityStatus.ACTIVE;
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
