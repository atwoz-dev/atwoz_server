package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.member.command.domain.member.event.PurchaseHeartGainedEvent;
import atwoz.atwoz.member.command.domain.member.exception.MemberNotActiveException;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
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

    @Embedded
    private PhoneNumber phoneNumber;

    @Embedded
    private KakaoId kakaoId;

    @Embedded
    @Getter
    private MemberProfile profile;

    @Getter
    private boolean isVip;

    @Embedded
    @Getter
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private ActivityStatus activityStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private PrimaryContactType primaryContactType;

    @Embedded
    @Getter
    private HeartBalance heartBalance;

    public static Member fromPhoneNumber(@NonNull String phoneNumber) {
        return Member.builder()
            .phoneNumber(PhoneNumber.from(phoneNumber))
            .activityStatus(ActivityStatus.ACTIVE)
            .heartBalance(HeartBalance.init())
            .isVip(false)
            .primaryContactType(PrimaryContactType.NONE)
            .build();
    }

    public void updateProfile(@NonNull MemberProfile profile) {
        this.profile = profile;
    }

    public boolean isProfileSettingNeeded() {
        if (profile == null) {
            return true;
        }
        return profile.isProfileSettingNeeded();
    }

    public String getKakaoId() {
        if (kakaoId == null) {
            return null;
        }
        return kakaoId.getValue();
    }

    public String getPhoneNumber() {
        return phoneNumber.getValue();
    }

    public void changeToDormant() {
        if (!isActive()) {
            throw new MemberNotActiveException();
        }
        activityStatus = ActivityStatus.DORMANT;
    }

    public void changePrimaryContactTypeToKakao(KakaoId kakaoId) {
        this.kakaoId = kakaoId;
        this.primaryContactType = PrimaryContactType.KAKAO;
    }

    public void changePrimaryContactTypeToPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = PhoneNumber.from(phoneNumber);
        this.primaryContactType = PrimaryContactType.PHONE_NUMBER;
    }

    public boolean isActive() {
        return activityStatus == ActivityStatus.ACTIVE;
    }

    public Gender getGender() {
        return profile.getGender();
    }

    public boolean isPermanentlySuspended() {
        return activityStatus == ActivityStatus.SUSPENDED_PERMANENTLY;
    }

    public void useHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.useHeart(heartAmount);
    }

    public void gainPurchaseHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.gainPurchaseHeart(heartAmount);
        Events.raise(PurchaseHeartGainedEvent.of(id, heartAmount.getAmount(), heartBalance.getMissionHeartBalance(),
            heartBalance.getPurchaseHeartBalance()));
    }

    public void gainMissionHeart(HeartAmount heartAmount) {
        heartBalance = heartBalance.gainMissionHeart(heartAmount);
    }
}
