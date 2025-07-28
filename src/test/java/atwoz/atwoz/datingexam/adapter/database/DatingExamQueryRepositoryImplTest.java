package atwoz.atwoz.datingexam.adapter.database;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamAnswerInfo;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamQuestionInfo;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamSubjectInfo;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.domain.DatingExamAnswer;
import atwoz.atwoz.datingexam.domain.DatingExamQuestion;
import atwoz.atwoz.datingexam.domain.DatingExamSubject;
import atwoz.atwoz.datingexam.domain.SubjectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QuerydslConfig.class, DatingExamQueryRepositoryImpl.class})
@DataJpaTest
class DatingExamQueryRepositoryImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DatingExamQueryRepository repo;

    private DatingExamSubject subj1;
    private DatingExamQuestion q11, q12;
    private DatingExamAnswer a111, a112, a113;

    @BeforeEach
    void setUp() {
        subj1 = DatingExamSubject.create("Subj1", SubjectType.OPTIONAL);
        em.persist(subj1);

        q11 = DatingExamQuestion.create(subj1.getId(), "q11");
        q12 = DatingExamQuestion.create(subj1.getId(), "q12");
        em.persist(q11);
        em.persist(q12);

        a111 = DatingExamAnswer.create(q11.getId(), "ans111");
        a112 = DatingExamAnswer.create(q11.getId(), "ans112");
        a113 = DatingExamAnswer.create(q12.getId(), "ans113");
        em.persist(a111);
        em.persist(a112);
        em.persist(a113);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("OPTIONAL 타입은 subjectType이 OPTIONAL인 시험 정보를 조회하면, 올바른 DTO를 반환한다.")
    void findOptionalExamInfoReturnsCorrectDto() {
        // when
        DatingExamInfoResponse response = repo.findDatingExamInfo(SubjectType.OPTIONAL);

        // then
        var expectedAnswer1 = new DatingExamAnswerInfo(a111.getId(), a111.getContent());
        var expectedAnswer2 = new DatingExamAnswerInfo(a112.getId(), a112.getContent());
        var expectedAnswer3 = new DatingExamAnswerInfo(a113.getId(), a113.getContent());

        var expectedQuestion1 = new DatingExamQuestionInfo(
            q11.getId(), q11.getContent(), List.of(expectedAnswer1, expectedAnswer2)
        );
        var expectedQuestion2 = new DatingExamQuestionInfo(
            q12.getId(), q12.getContent(), List.of(expectedAnswer3)
        );
        var expectedSubject = new DatingExamSubjectInfo(
            subj1.getId(),
            subj1.getType().name(),
            subj1.getName(),
            List.of(expectedQuestion1, expectedQuestion2)
        );
        var expected = new DatingExamInfoResponse(List.of(expectedSubject));

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("subjectType에 맞는 데이터가 없는 경우, 조회 결과가 빈 리스트를 반환한다.")
    void findRequiredExamInfoReturnsEmpty() {
        // when
        DatingExamInfoResponse response = repo.findDatingExamInfo(SubjectType.REQUIRED);

        // then
        assertThat(response.subjects()).isEmpty();
    }
}
