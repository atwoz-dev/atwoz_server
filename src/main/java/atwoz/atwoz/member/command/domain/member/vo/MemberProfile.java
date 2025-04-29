package atwoz.atwoz.member.command.domain.member.vo;

import atwoz.atwoz.member.command.domain.member.*;
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

    @Embedded
    private YearOfBirth yearOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Gender gender;

    private Integer height;

    @ElementCollection
    @CollectionTable(name = "member_hobbies", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "hobby_name")
    @Enumerated(EnumType.STRING)
    private Set<Hobby> hobbies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Mbti mbti;

    @Embedded
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

    /**
     * Constructs a MemberProfile with the specified personal attributes.
     *
     * Converts the provided year of birth integer into a YearOfBirth object.
     *
     * @param nickname the user's nickname
     * @param yearOfBirth the user's year of birth as an integer
     * @param gender the user's gender
     * @param height the user's height in centimeters
     * @param mbti the user's MBTI personality type
     * @param region the user's region of residence
     * @param religion the user's religion
     * @param smokingStatus the user's smoking status
     * @param drinkingStatus the user's drinking status
     * @param highestEducation the user's highest education level
     * @param job the user's job
     * @param hobbies the user's hobbies
     */
    @Builder
    private MemberProfile(
            Nickname nickname, Integer yearOfBirth, Gender gender, Integer height,
            Mbti mbti, Region region, Religion religion,
            SmokingStatus smokingStatus, DrinkingStatus drinkingStatus, HighestEducation highestEducation,
            Job job, Set<Hobby> hobbies
    ) {
        this.nickname = nickname;
        this.yearOfBirth = YearOfBirth.from(yearOfBirth);
        this.gender = gender;
        this.height = height;
        this.mbti = mbti;
        this.region = region;
        this.religion = religion;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.highestEducation = highestEducation;
        this.job = job;
        this.hobbies = hobbies;
    }

    /**
     * Determines whether the member profile requires additional setup.
     *
     * @return true if any required profile field is unset or empty; false otherwise
     */
    public boolean isProfileSettingNeeded() {
        return nickname == null || yearOfBirth == null || yearOfBirth.getValue() == null || gender == null || height == null || job == null ||
                hobbies == null || hobbies.isEmpty() || mbti == null || region == null || religion == null ||
                smokingStatus == null || drinkingStatus == null || highestEducation == null;
    }
}
