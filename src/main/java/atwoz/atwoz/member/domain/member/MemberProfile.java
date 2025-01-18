package atwoz.atwoz.member.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberProfile {

    @Embedded
    private Nickname nickname;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    private Integer height;

    private Long jobId;

    @ElementCollection
    @CollectionTable(name = "member_hobbies", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "hobby_id")
    private Set<Long> hobbyIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Religion religion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private SmokingStatus smokingStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private DrinkingStatus drinkingStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private HighestEducation highestEducation;

    @Builder
    private MemberProfile(
            Nickname nickname, Integer age, Gender gender, Integer height, Long jobId,
            Set<Long> hobbyIds, Mbti mbti, Region region, Religion religion,
            SmokingStatus smokingStatus, DrinkingStatus drinkingStatus, HighestEducation highestEducation
    ) {
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.jobId = jobId;
        this.hobbyIds = hobbyIds;
        this.mbti = mbti;
        this.region = region;
        this.religion = religion;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.highestEducation = highestEducation;
    }

    public boolean isProfileSettingNeeded() {
        return nickname == null || age == null || gender == null || height == null || jobId == null ||
                hobbyIds == null || hobbyIds.isEmpty() || mbti == null || region == null || religion == null ||
                smokingStatus == null || drinkingStatus == null || highestEducation == null;
    }
}
