package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class IntroductionSearchCondition {
    private final Set<Long> excludedMemberIds;
    private final Integer minAge;
    private final Integer maxAge;
    private final Set<String> hobbies;
    private final Set<String> cities;
    private final String religion;
    private final String smokingStatus;
    private final String drinkingStatus;
    private final String memberGrade;
    private final String gender;
    private final LocalDateTime joinedAfter;

    public static IntroductionSearchCondition ofGrade(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Gender gender,
            Grade grade
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbies(),
                memberIdeal.getCities(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                grade,
                gender,
                null
        );
    }

    public static IntroductionSearchCondition ofHobbies(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Gender gender,
            Set<Hobby> hobbies
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                hobbies,
                memberIdeal.getCities(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                gender,
                null
        );
    }

    public static IntroductionSearchCondition ofReligion(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Gender gender,
            Religion religion
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbies(),
                memberIdeal.getCities(),
                religion,
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                gender,
                null
        );
    }

    public static IntroductionSearchCondition ofCity(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Gender gender,
            City city
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbies(),
                Set.of(city),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                gender,
                null
        );
    }

    public static IntroductionSearchCondition ofJoinDate(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Gender gender,
            LocalDateTime joinedAfter
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbies(),
                memberIdeal.getCities(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                gender,
                joinedAfter
        );
    }

    private IntroductionSearchCondition(
            Set<Long> excludedMemberIds,
            AgeRange ageRange,
            Set<Hobby> hobbies,
            Set<City> cities,
            Religion religion,
            SmokingStatus smokingStatus,
            DrinkingStatus drinkingStatus,
            Grade memberGrade,
            @NonNull Gender gender,
            LocalDateTime joinedAfter
    ) {
        this.excludedMemberIds = excludedMemberIds;
        this.minAge = (ageRange != null) ? ageRange.getMinAge() : null;
        this.maxAge = (ageRange != null) ? ageRange.getMaxAge() : null;
        this.hobbies = hobbies.stream().map(Hobby::name).collect(Collectors.toSet());
        this.cities = cities.stream().map(City::name).collect(Collectors.toSet());
        this.religion = (religion != null) ? religion.name() : null;
        this.smokingStatus = (smokingStatus != null) ? smokingStatus.name() : null;
        this.drinkingStatus = (drinkingStatus != null) ? drinkingStatus.name() : null;
        this.memberGrade = (memberGrade != null) ? memberGrade.name() : null;
        this.gender = gender.name();
        this.joinedAfter = joinedAfter;
    }
}