package atwoz.atwoz.member.command.domain.introduction;

import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.DrinkingStatus;
import atwoz.atwoz.member.command.domain.member.Region;
import atwoz.atwoz.member.command.domain.member.Religion;
import atwoz.atwoz.member.command.domain.member.SmokingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class MemberIdealTest {

    @Nested
    @DisplayName("init 메서드 테스트")
    class InitTest {

        @Test
        @DisplayName("memberId가 null이면 예외를 던진다.")
        void throwsExceptionWhenMemberIdIsNull() {
            // given
            Long memberId = null;

            // when, then
            assertThatThrownBy(() -> MemberIdeal.init(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("정상적으로 MemberIdeal 객체를 생성한다.")
        void createsMemberIdealObject() {
            // given
            Long memberId = 1L;

            // when
            MemberIdeal memberIdeal = MemberIdeal.init(memberId);

            // then
            assertThat(memberIdeal.getMemberId()).isEqualTo(memberId);
            assertThat(memberIdeal.getAgeRange()).isEqualTo(AgeRange.init());
            assertThat(memberIdeal.getHobbyIds()).isEmpty();
            assertThat(memberIdeal.getRegions()).isEmpty();
            assertThat(memberIdeal.getReligion()).isNull();
            assertThat(memberIdeal.getSmokingStatus()).isNull();
            assertThat(memberIdeal.getDrinkingStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("update 메서드 테스트")
    class UpdateTest {

        private Set<Long> hobbyIds;
        private AgeRange ageRange;
        private Set<Region> regions;
        private Religion religion;
        private SmokingStatus smokingStatus;
        private DrinkingStatus drinkingStatus;

        @BeforeEach
        void setUp() {
            ageRange = AgeRange.of(20, 30);
            regions = Set.of(Region.SEOUL);
            religion = Religion.CHRISTIAN;
            smokingStatus = SmokingStatus.VAPE;
            drinkingStatus = DrinkingStatus.SOCIAL;
            hobbyIds = Set.of(1L, 2L);
        }

        @Test
        @DisplayName("ageRange가 null이면 예외를 던진다.")
        void throwsExceptionWhenAgeRangeIsNull() {
            // given
            MemberIdeal memberIdeal = MemberIdeal.init(1L);
            ageRange = null;

            // when, then
            assertThatThrownBy(() -> memberIdeal.update(ageRange, hobbyIds, regions, religion, smokingStatus, drinkingStatus))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("hobbyIds가 null이면 예외를 던진다.")
        void throwsExceptionWhenHobbyIdsIsNull() {
            // given
            MemberIdeal memberIdeal = MemberIdeal.init(1L);
            hobbyIds = null;

            // when, then
            assertThatThrownBy(() -> memberIdeal.update(ageRange, hobbyIds, regions, religion, smokingStatus, drinkingStatus))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("regions가 null이면 예외를 던진다.")
        void throwsExceptionWhenRegionsIsNull() {
            // given
            MemberIdeal memberIdeal = MemberIdeal.init(1L);
            regions = null;

            // when, then
            assertThatThrownBy(() -> memberIdeal.update(ageRange, hobbyIds, regions, religion, smokingStatus, drinkingStatus))
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"religion", "smokingStatus", "drinkingStatus", "none"})
        @DisplayName("ageRange, hobbyIds, regions 외의 다른 파라미터가 null이면 update한다.")
        void updatesMemberIdeal(String nullParameter) {
            // given
            MemberIdeal memberIdeal = MemberIdeal.init(1L);
            if (nullParameter.equals("religion")) {
                religion = null;
            } else if (nullParameter.equals("smokingStatus")) {
                smokingStatus = null;
            } else if (nullParameter.equals("drinkingStatus")) {
                drinkingStatus = null;
            } else if (nullParameter.equals("none")) {
                // do nothing
            }

            // when
            memberIdeal.update(ageRange, hobbyIds, regions, religion, smokingStatus, drinkingStatus);

            // then
            assertThat(memberIdeal.getAgeRange()).isEqualTo(ageRange);
            assertThat(memberIdeal.getHobbyIds()).isEqualTo(hobbyIds);
            assertThat(memberIdeal.getRegions()).isEqualTo(regions);
            assertThat(memberIdeal.getReligion()).isEqualTo(religion);
            assertThat(memberIdeal.getSmokingStatus()).isEqualTo(smokingStatus);
            assertThat(memberIdeal.getDrinkingStatus()).isEqualTo(drinkingStatus);
        }
    }
}