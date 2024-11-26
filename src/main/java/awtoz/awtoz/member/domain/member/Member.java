package awtoz.awtoz.member.domain.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NickName nickName;

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
        if (this.nickName == null || gender == null || region == null || age == null || height == null || mbti == null) {
            return true;
        }
        return false;
    }

}
