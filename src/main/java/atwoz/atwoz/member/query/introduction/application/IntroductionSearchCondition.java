package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class IntroductionSearchCondition {
    private final Set<Long> excludedMemberIds;
    private final Integer minAge;
    private final Integer maxAge;
    private final Set<Long> hobbyIds;
    private final String region;
    private final String religion;
    private final String smokingStatus;
    private final String drinkingStatus;
    private final String memberGrade;
    private final LocalDateTime joinedAfter;

    public static IntroductionSearchCondition ofGrade(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Grade grade
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbyIds(),
                memberIdeal.getRegion(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                grade,
                null
        );
    }

    public static IntroductionSearchCondition ofHobbyIds(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Set<Long> hobbyIds
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                hobbyIds,
                memberIdeal.getRegion(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                null
        );
    }

    public static IntroductionSearchCondition ofReligion(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Religion religion
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbyIds(),
                memberIdeal.getRegion(),
                religion,
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                null
        );
    }

    public static IntroductionSearchCondition ofRegion(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            Region region
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbyIds(),
                region,
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                null
        );
    }

    public static IntroductionSearchCondition ofJoinDate(
            Set<Long> excludedMemberIds,
            MemberIdeal memberIdeal,
            LocalDateTime joinedAfter
    ) {
        return new IntroductionSearchCondition(
                excludedMemberIds,
                memberIdeal.getAgeRange(),
                memberIdeal.getHobbyIds(),
                memberIdeal.getRegion(),
                memberIdeal.getReligion(),
                memberIdeal.getSmokingStatus(),
                memberIdeal.getDrinkingStatus(),
                null,
                joinedAfter
        );
    }

    private IntroductionSearchCondition(
            Set<Long> excludedMemberIds,
            AgeRange ageRange,
            Set<Long> hobbyIds,
            Region region,
            Religion religion,
            SmokingStatus smokingStatus,
            DrinkingStatus drinkingStatus,
            Grade memberGrade,
            LocalDateTime joinedAfter
    ) {
        this.excludedMemberIds = excludedMemberIds;
        this.minAge = (ageRange != null) ? ageRange.getMinAge() : null;
        this.maxAge = (ageRange != null) ? ageRange.getMaxAge() : null;
        this.hobbyIds = hobbyIds;
        this.region = (region != null) ? region.name() : null;
        this.religion = (religion != null) ? religion.name() : null;
        this.smokingStatus = (smokingStatus != null) ? smokingStatus.name() : null;
        this.drinkingStatus = (drinkingStatus != null) ? drinkingStatus.name() : null;
        this.memberGrade = (memberGrade != null) ? memberGrade.name() : null;
        this.joinedAfter = joinedAfter;
    }
}