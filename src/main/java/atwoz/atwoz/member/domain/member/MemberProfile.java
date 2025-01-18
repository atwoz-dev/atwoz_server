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

    private Integer age;

    private Integer height;

    private Long jobId;

    @ElementCollection
    @CollectionTable(name = "member_hobbies", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "hobby_id")
    private Set<Long> hobbyIds = new HashSet<>();

    @Embedded
    private Nickname nickname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private SmokingStatus smokingStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private DrinkingStatus drinkingStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Religion religion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private HighestEducation highestEducation;

    @Builder
    private MemberProfile(
            Integer age, Integer height, Long jobId, Set<Long> hobbyIds,
            Nickname nickname, Gender gender, Mbti mbti, Region region,
            SmokingStatus smokingStatus, DrinkingStatus drinkingStatus,
            Religion religion, HighestEducation highestEducation
    ) {
        this.age = age;
        this.height = height;
        this.jobId = jobId;
        this.hobbyIds = hobbyIds;
        this.nickname = nickname;
        this.gender = gender;
        this.mbti = mbti;
        this.region = region;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.religion = religion;
        this.highestEducation = highestEducation;
    }

    public boolean isProfileSettingNeeded() {
        return age == null || height == null || jobId == null || hobbyIds == null || hobbyIds.isEmpty() ||
                nickname == null || gender == null || mbti == null || region == null ||
                smokingStatus == null || drinkingStatus == null || religion == null || highestEducation == null;
    }
}
