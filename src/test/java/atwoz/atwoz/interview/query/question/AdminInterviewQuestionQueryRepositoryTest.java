package atwoz.atwoz.interview.query.question;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.query.question.view.AdminInterviewQuestionView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Import({QuerydslConfig.class, AdminInterviewQuestionQueryRepository.class})
class AdminInterviewQuestionQueryRepositoryTest {

    @Autowired
    private AdminInterviewQuestionQueryRepository adminInterviewQuestionQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("인터뷰 질문 페이지네이션 조회 테스트")
    class AdminInterviewQuestionPageTest {

        @Test
        @DisplayName("인터뷰 질문 페이지네이션 조회")
        void findAdminInterviewQuestionPage() {
            // given
            InterviewQuestion question1 = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            InterviewQuestion question2 = InterviewQuestion.of("질문2", InterviewCategory.SOCIAL, false);
            InterviewQuestion question3 = InterviewQuestion.of("질문3", InterviewCategory.ROMANTIC, true);
            entityManager.persist(question1);
            entityManager.persist(question2);
            entityManager.persist(question3);
            entityManager.flush();
            List<InterviewQuestion> sortedQuestions = List.of(question1, question2, question3)
                .stream()
                .sorted(Comparator.comparing(InterviewQuestion::getId).reversed())
                .collect(Collectors.toList());
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<AdminInterviewQuestionView> result = adminInterviewQuestionQueryRepository.findAdminInterviewQuestionPage(
                pageRequest);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent().get(0).id()).isEqualTo(sortedQuestions.get(0).getId());
            assertThat(result.getContent().get(0).content()).isEqualTo(sortedQuestions.get(0).getContent());
            assertThat(result.getContent().get(0).category()).isEqualTo(sortedQuestions.get(0).getCategory().name());
            assertThat(result.getContent().get(0).isPublic()).isEqualTo(sortedQuestions.get(0).isPublic());
            assertThat(result.getContent().get(0).createdAt()).isCloseTo(sortedQuestions.get(0).getCreatedAt(),
                within(1, ChronoUnit.MICROS));
            assertThat(result.getContent().get(1).id()).isEqualTo(sortedQuestions.get(1).getId());
            assertThat(result.getContent().get(2).id()).isEqualTo(sortedQuestions.get(2).getId());
        }
    }

    @Nested
    @DisplayName("인터뷰 질문 단건 조회 테스트")
    class AdminInterviewQuestionFindTest {

        @Test
        @DisplayName("인터뷰 질문 단건 조회")
        void findAdminInterviewQuestionById() {
            // given
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            entityManager.flush();

            // when
            Optional<AdminInterviewQuestionView> result = adminInterviewQuestionQueryRepository.findAdminInterviewQuestionById(
                question.getId());

            // then
            assertThat(result).isNotNull();
            AdminInterviewQuestionView view = result.get();
            assertThat(view.id()).isEqualTo(question.getId());
            assertThat(view.content()).isEqualTo(question.getContent());
            assertThat(view.category()).isEqualTo(question.getCategory().name());
            assertThat(view.isPublic()).isEqualTo(question.isPublic());
            assertThat(view.createdAt()).isCloseTo(question.getCreatedAt(), within(1, ChronoUnit.MICROS));
        }

        @Test
        @DisplayName("존재하지 않는 인터뷰 질문 단건 조회")
        void findAdminInterviewQuestionByIdNotFound() {
            // given
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            entityManager.flush();

            // when
            Optional<AdminInterviewQuestionView> result = adminInterviewQuestionQueryRepository.findAdminInterviewQuestionById(
                question.getId() + 1);

            // then
            assertThat(result).isEmpty();
        }
    }

}