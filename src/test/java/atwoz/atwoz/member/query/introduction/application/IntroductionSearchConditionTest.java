package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntroductionSearchConditionTest {

    private final Set<Long> excludedIds = Set.of(1L, 2L);
    private final AgeRange ageRange = AgeRange.of(20, 30);
    private final Set<Long> hobbyIds = Set.of(10L, 20L);
    private final Region region = Region.SEOUL;
    private final Religion religion = Religion.CHRISTIAN;
    private final SmokingStatus smokingStatus = SmokingStatus.NONE;
    private final DrinkingStatus drinkingStatus = DrinkingStatus.NONE;

    MemberIdeal getMockedMemberIdeal() {
        MemberIdeal ideal = mock(MemberIdeal.class);
        when(ideal.getAgeRange()).thenReturn(ageRange);
        when(ideal.getHobbyIds()).thenReturn(hobbyIds);
        when(ideal.getRegion()).thenReturn(region);
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
    }

    @Test
    @DisplayName("ofGrade 메서드 테스트")
    void ofGradeTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Grade grade = Grade.DIAMOND;

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofGrade(excludedIds, ideal, grade);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbyIds()).isEqualTo(hobbyIds);
        assertThat(condition.getRegion()).isEqualTo(region.name());
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isEqualTo(grade.name());
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofHobbyIds 메서드 테스트")
    void ofHobbyIdsTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Set<Long> memberHobbyIds = Set.of(30L, 40L);

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofHobbyIds(excludedIds, ideal, memberHobbyIds);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbyIds()).isEqualTo(memberHobbyIds);
        assertThat(condition.getRegion()).isEqualTo(region.name());
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
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofReligion(excludedIds, ideal, memberReligion);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbyIds()).isEqualTo(hobbyIds);
        assertThat(condition.getRegion()).isEqualTo(region.name());
        assertThat(condition.getReligion()).isEqualTo(memberReligion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isNull();
    }

    @Test
    @DisplayName("ofRegion 메서드 테스트")
    void ofRegionTest() {
        // given
        MemberIdeal ideal = getMockedMemberIdeal();
        Region memberRegion = Region.DAEJEON;

        // when
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofRegion(excludedIds, ideal, memberRegion);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbyIds()).isEqualTo(hobbyIds);
        assertThat(condition.getRegion()).isEqualTo(memberRegion.name());
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
        IntroductionSearchCondition condition = IntroductionSearchCondition.ofJoinDate(excludedIds, ideal, joinedAfter);

        // then
        assertCommonFields(condition);
        assertThat(condition.getHobbyIds()).isEqualTo(hobbyIds);
        assertThat(condition.getRegion()).isEqualTo(region.name());
        assertThat(condition.getReligion()).isEqualTo(religion.name());
        assertThat(condition.getMemberGrade()).isNull();
        assertThat(condition.getJoinedAfter()).isEqualTo(joinedAfter);
    }
}