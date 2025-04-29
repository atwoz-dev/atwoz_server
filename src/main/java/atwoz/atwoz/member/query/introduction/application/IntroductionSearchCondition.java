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

    /**
     * Creates a search condition for member introductions using the specified excluded member IDs, member ideal preferences, gender, and member grade.
     *
     * The search criteria are populated from the provided {@code MemberIdeal} object, with the member grade and gender explicitly set.
     *
     * @param excludedMemberIds set of member IDs to exclude from search results
     * @param memberIdeal member's ideal preferences used to derive search criteria
     * @param gender gender to filter by
     * @param grade member grade to filter by
     * @return an {@code IntroductionSearchCondition} configured with the specified criteria
     */
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

    /**
     * Creates a search condition for member introductions using the specified excluded member IDs, member ideal preferences, gender, and a set of hobbies.
     *
     * The resulting condition filters introductions by the provided hobbies and other criteria derived from the given {@code MemberIdeal} object.
     *
     * @param excludedMemberIds set of member IDs to exclude from the search
     * @param memberIdeal member's ideal preferences used to derive additional search criteria
     * @param gender gender to filter by
     * @param hobbies set of hobbies to include in the search
     * @return an {@code IntroductionSearchCondition} configured with the specified and derived criteria
     */
    public static IntroductionSearchCondition ofHobbyIds(
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

    /**
     * Creates an IntroductionSearchCondition using the specified excluded member IDs, member ideal preferences, gender, and religion.
     *
     * The resulting condition filters introductions by the given religion, while other criteria (age range, hobbies, cities, smoking and drinking status) are derived from the provided MemberIdeal.
     *
     * @param excludedMemberIds set of member IDs to exclude from the search
     * @param memberIdeal member's ideal preferences for filtering criteria
     * @param gender gender to filter by
     * @param religion religion to filter by
     * @return a new IntroductionSearchCondition with the specified and derived criteria
     */
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

    /**
     * Creates a search condition for member introductions filtered by a specific city.
     *
     * The search criteria are based on the provided member ideal and gender, with the city filter applied. Excluded member IDs are omitted from the results.
     *
     * @param excludedMemberIds set of member IDs to exclude from the search
     * @param memberIdeal the member's ideal preferences used for filtering
     * @param gender the gender to filter by
     * @param city the city to include in the search filter
     * @return an IntroductionSearchCondition configured with the specified city and other criteria
     */
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

    /**
     * Creates a search condition for member introductions filtered by join date and other ideal criteria.
     *
     * @param excludedMemberIds set of member IDs to exclude from the search
     * @param memberIdeal member's ideal criteria used to populate age range, hobbies, cities, religion, smoking, and drinking status
     * @param gender gender to filter by
     * @param joinedAfter only include members who joined after this date and time
     * @return an IntroductionSearchCondition with the specified filters applied
     */
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

    /**
     * Constructs an IntroductionSearchCondition with the specified filtering criteria.
     *
     * Converts domain objects such as hobbies, cities, religion, smoking status, drinking status, member grade, and gender to their string representations for internal use. Null values are handled by assigning null to the corresponding fields.
     *
     * @param excludedMemberIds set of member IDs to exclude from search results
     * @param ageRange age range filter; if null, no age filtering is applied
     * @param hobbies set of hobbies to filter by; converted to string names
     * @param cities set of cities to filter by; converted to string names
     * @param religion religion filter; if null, no religion filtering is applied
     * @param smokingStatus smoking status filter; if null, no smoking status filtering is applied
     * @param drinkingStatus drinking status filter; if null, no drinking status filtering is applied
     * @param memberGrade member grade filter; if null, no grade filtering is applied
     * @param gender gender filter (must not be null)
     * @param joinedAfter filters members who joined after this date; if null, no join date filtering is applied
     */
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