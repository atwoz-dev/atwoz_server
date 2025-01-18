package atwoz.atwoz.member.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberProfile {

    private Integer age;

    private Integer height;

    private Long jobId;

    @OneToMany(mappedBy = "memberId", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MemberHobby> memberHobbyList = new ArrayList<>();

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
    private ReligionStatus religionStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private HighestEducation highestEducation;

    @Builder
    private MemberProfile(
            Integer age, Integer height, Long jobId, List<MemberHobby> memberHobbyList,
            Nickname nickname, Gender gender, Mbti mbti, Region region,
            SmokingStatus smokingStatus, DrinkingStatus drinkingStatus,
            ReligionStatus religionStatus, HighestEducation highestEducation
    ) {
        this.age = age;
        this.height = height;
        this.jobId = jobId;
        this.memberHobbyList = memberHobbyList;
        this.nickname = nickname;
        this.gender = gender;
        this.mbti = mbti;
        this.region = region;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.religionStatus = religionStatus;
        this.highestEducation = highestEducation;
    }

    public boolean isProfileSettingNeeded() {
        return age == null || height == null || jobId == null || memberHobbyList == null || memberHobbyList.isEmpty() ||
                nickname == null || gender == null || mbti == null || region == null ||
                smokingStatus == null || drinkingStatus == null ||  religionStatus == null || highestEducation == null;
    }
}
