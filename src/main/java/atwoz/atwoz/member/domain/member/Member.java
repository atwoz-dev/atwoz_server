package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ActivityStatus activityStatus;

    public static Member createFromPhoneNumber(String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .activityStatus(ActivityStatus.ACTIVE)
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

}
