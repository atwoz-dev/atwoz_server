package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntroductionSearchConditionTest {

    private final Set<Long> excludedIds = Set.of(1L, 2L);
    private final AgeRange ageRange = AgeRange.of(20, 30);
    private final Set<Hobby> hobbies = Set.of(Hobby.CAMPING, Hobby.ANIMATION);
    private final Set<City> cities = Set.of(City.SEOUL);
    private final Religion religion = Religion.CHRISTIAN;
    private final SmokingStatus smokingStatus = SmokingStatus.NONE;
    private final DrinkingStatus drinkingStatus = DrinkingStatus.NONE;
    private final Gender gender = Gender.MALE;

    MemberIdeal getMockedMemberIdeal() {
        MemberIdeal ideal = mock(MemberIdeal.class);
        when(ideal.getAgeRange()).thenReturn(ageRange);
        when(ideal.getHobbies()).thenReturn(hobbies);
        when(ideal.getCities()).thenReturn(cities);
        when(ideal.getReligion()).thenReturn(religion);
        when(ideal.getSmokingStatus()).thenReturn(smokingStatus);
        when(ideal.getDrinkingStatus()).thenReturn(drinkingStatus);
        return ideal;
    }

    void assertCommonFields(IntroductionSearchCondition condition) {
        assertThat(condition.getExcludedMemberIds()).isEqualTo(excludedIds);
        assertThat(condition.getMinAge()).isEqualTo(ageRange.getMinAge());
        assertThat(condition.getMaxAge()).isEqualTo(ageRange.getMaxAge());
        assertThat(condition.getSmokingStatus()).isEqualTo(smokingStatus.name());
        assertThat(condition.getDrinkingStatus()).isEqualTo(drinkingStatus.name());
        assertThat(condition.getGender()).isEqualTo(gender.name());
    }

    @Test
    @DisplayName("ofGrade 메서드 테스트")
    void ofGradeTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Grade grade = Grade.DIAMOND;

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofGrade(excludedIds, ideal, gender, grade);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbies()).isEqualTo(hobbies.stream().map(Hobby::name).collect(Collectors.toSet()));
        assertThat(condition.getCities()).isEqualTo(cities.stream().map(City::name).collect(Collectors.toSet()));
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isEqualTo(grade.name());
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofHobbyIds 메서드 테스트")
    void ofHobbiesTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Set<Hobby> memberHobbies = Set.of(Hobby.CAMPING, Hobby.ANIMATION);

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofHobbies(excludedIds, ideal, gender, memberHobbies);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbies()).isEqualTo(memberHobbies.stream().map(Hobby::name).collect(Collectors.toSet()));
        assertThat(condition.getCities()).isEqualTo(cities.stream().map(City::name).collect(Collectors.toSet()));
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofReligion 메서드 테스트")
    void ofReligionTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Religion memberReligion = Religion.NONE;

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofReligion(excludedIds, ideal, gender, memberReligion);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbies()).isEqualTo(hobbies.stream().map(Hobby::name).collect(Collectors.toSet()));
        assertThat(condition.getCities()).isEqualTo(cities.stream().map(City::name).collect(Collectors.toSet()));
        assertThat(condition.getReligion()).isEqualTo(memberReligion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofRegion 메서드 테스트")
    void ofRegionTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        City memberRegion = City.DAEJEON;

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofCity(excludedIds, ideal, gender, memberRegion);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbies()).isEqualTo(hobbies.stream().map(Hobby::name).collect(Collectors.toSet()));
        assertThat(condition.getCities()).isEqualTo(Set.of(memberRegion.name()));
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofJoinedAfter 메서드 테스트")
    void ofJoinedAfterTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        LocalDateTime joinedAfter = LocalDateTime.now();

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofJoinDate(excludedIds, ideal, gender, joinedAfter);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbies()).isEqualTo(hobbies.stream().map(Hobby::name).collect(Collectors.toSet()));
        assertThat(condition.getCities()).isEqualTo(cities.stream().map(City::name).collect(Collectors.toSet()));
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isEqualTo(joinedAfter);
    }
}