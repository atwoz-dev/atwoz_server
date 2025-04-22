package atwoz.atwoz.member.query.introduction.intra;


import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.hobby.Hobby;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.query.introduction.application.MemberIdealView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, MemberIdealQueryRepository.class})
class MemberIdealQueryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberIdealQueryRepository memberIdealQueryRepository;

    @Test
    @DisplayName("memberIdeal이 존재하지 않는 경우 Optional.empty()를 반환한다.")
    void returnOptionalEmptyWhenMemberIdealIsNotExists() {
        // given
        long memberId = 1L;

        // when
        var memberIdeal = memberIdealQueryRepository.findMemberIdealByMemberId(memberId);

        // then
        assertThat(memberIdeal).isEmpty();
    }

    @Test
    @DisplayName("memberIdeal이 존재하는 경우 MemberIdealView를 반환한다.")
    void returnMemberIdealViewWhenMemberIdealIsExists() {
        // given
        long memberId = 1L;
        MemberIdeal memberIdeal = MemberIdeal.init(memberId);
        AgeRange ageRange = AgeRange.of(20, 30);
        Hobby hobby1 = Hobby.from("취미1");
        Hobby hobby2 = Hobby.from("취미2");
        entityManager.persist(hobby1);
        entityManager.persist(hobby2);
        Set<Long> hobbyIds = Set.of(hobby1.getId(), hobby2.getId());
        Set<City> cities = Set.of(City.SEOUL);
        Religion religion = Religion.CHRISTIAN;
        SmokingStatus smokingStatus = SmokingStatus.NONE;
        DrinkingStatus drinkingStatus = DrinkingStatus.NONE;
        memberIdeal.update(ageRange, hobbyIds, cities, religion, smokingStatus, drinkingStatus);
        entityManager.persist(memberIdeal);
        entityManager.flush();

        // when
        MemberIdealView memberIdealView = memberIdealQueryRepository.findMemberIdealByMemberId(memberId).get();

        // then
        assertThat(memberIdealView.minAge()).isEqualTo(ageRange.getMinAge());
        assertThat(memberIdealView.maxAge()).isEqualTo(ageRange.getMaxAge());
        assertThat(memberIdealView.hobbies()).containsExactlyInAnyOrder(hobby1.getName(), hobby2.getName());
        assertThat(memberIdealView.regions()).containsExactlyInAnyOrder(cities.stream().map(City::name).toArray(String[]::new));
        assertThat(memberIdealView.religion()).isEqualTo(religion.name());
        assertThat(memberIdealView.smokingStatus()).isEqualTo(smokingStatus.name());
        assertThat(memberIdealView.drinkingStatus()).isEqualTo(drinkingStatus.name());
    }
}