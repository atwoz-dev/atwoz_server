package atwoz.atwoz.member.query;

import atwoz.atwoz.hobby.domain.Hobby;
import atwoz.atwoz.job.domain.Job;
import atwoz.atwoz.member.command.domain.member.DrinkingStatus;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@TestConfiguration
public class TestConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Bean
    @Transactional
    public TestData testData() {
        Job job = Job.from("직업1");
        entityManager.persist(job);

        Hobby hobby1 = Hobby.from("취미1");
        Hobby hobby2 = Hobby.from("취미2");
        entityManager.persist(hobby1);
        entityManager.persist(hobby2);

        entityManager.flush();

        Set<Long> hobbyIds = Set.of(hobby1.getId(), hobby2.getId());

        Member member = Member.fromPhoneNumber("01012345678");

        MemberProfile updateProfile = MemberProfile.builder()
                .age(10)
                .height(20)
                .drinkingStatus(DrinkingStatus.NONE)
                .jobId(job.getId())
                .hobbyIds(hobbyIds)
                .build();

        member.updateProfile(updateProfile);

        entityManager.persist(member);
        entityManager.flush();

        return new TestData(job, List.of(hobby1, hobby2), member);
    }

    public static class TestData {
        private final Job job;
        private final List<Hobby> hobbies;
        private final Member member;

        public TestData(Job job, List<Hobby> hobbies, Member member) {
            this.job = job;
            this.hobbies = hobbies;
            this.member = member;
        }

        public Job getJob() {
            return job;
        }

        public List<Hobby> getHobbies() {
            return hobbies;
        }

        public Member getMember() {
            return member;
        }
    }
}
