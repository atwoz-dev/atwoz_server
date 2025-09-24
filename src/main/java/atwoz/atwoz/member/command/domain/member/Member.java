package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.member.command.domain.member.event.*;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_deleted_at", columnList = "deletedAt")
})
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

    @Getter
    @Builder.Default
    private boolean isProfilePublic = false;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
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

    @Getter
    @Builder.Default
    private boolean isDatingExamSubmitted = false;

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
        activityStatus = ActivityStatus.DORMANT;
        Events.raise(MemberBecameDormantEvent.from(id));
    }

    public void changeToActive() {
        activityStatus = ActivityStatus.ACTIVE;
        Events.raise(MemberBecameActiveEvent.from(id));
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

    public void gainMissionHeart(HeartAmount heartAmount, String actionType) {
        heartBalance = heartBalance.gainMissionHeart(heartAmount);
        Events.raise(MissionHeartGainedEvent.of(id, heartAmount.getAmount(), heartBalance.getMissionHeartBalance(),
            heartBalance.getPurchaseHeartBalance(), actionType));
    }

    public void updateGrade(@NonNull Grade grade) {
        this.grade = grade;
    }

    public void updateSetting(
        @NonNull Grade grade,
        boolean isProfilePublic,
        @NonNull ActivityStatus activityStatus,
        boolean isVip,
        boolean isPushNotificationEnabled
    ) {
        this.grade = grade;
        this.isProfilePublic = isProfilePublic;
        changeActivityStatus(activityStatus);
        this.isVip = isVip;
        Events.raise(MemberSettingUpdatedEvent.of(id, isPushNotificationEnabled));
    }

    public void publishProfile() {
        isProfilePublic = true;
    }

    public void nonPublishProfile() {
        isProfilePublic = false;
    }

    public void changeActivityStatus(@NonNull ActivityStatus activityStatus) {
        switch (activityStatus) {
            case ActivityStatus.DORMANT -> changeToDormant();
            case ActivityStatus.ACTIVE -> changeToActive();
            default -> this.activityStatus = activityStatus;
        }
    }

    public void markDatingExamSubmitted() {
        if (isDatingExamSubmitted) {
            throw new IllegalStateException("이미 연애 모의고사를 제출한 멤버입니다. member id: " + id);
        }
        isDatingExamSubmitted = true;
    }

    public boolean hasSubmittedDatingExam() {
        return isDatingExamSubmitted;
    }
}
