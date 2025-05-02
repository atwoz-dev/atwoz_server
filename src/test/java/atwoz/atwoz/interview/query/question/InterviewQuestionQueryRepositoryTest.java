package atwoz.atwoz.interview.query.question;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.query.question.view.InterviewQuestionView;
import atwoz.atwoz.member.command.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, InterviewQuestionQueryRepository.class})
class InterviewQuestionQueryRepositoryTest {

    @Autowired
    private InterviewQuestionQueryRepository interviewQuestionQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("멤버 인터뷰 질문 조회 테스트")
    class MemberInterviewQuestionTest {

        @Test
        @DisplayName("멤버 인터뷰 질문 조회 파라미터 테스트")
        void findAllQuestionByCategoryWithMemberId() {
            // given
            Member member = Member.fromPhoneNumber("01012345678");
            entityManager.persist(member);
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            entityManager.flush();
            InterviewAnswer answer = InterviewAnswer.of(question.getId(), member.getId(), "답변1");
            entityManager.persist(answer);
            entityManager.flush();

            // when
            List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
                question.getCategory().name(), member.getId());

            // then
            assertThat(views).hasSize(1);
            InterviewQuestionView view = views.get(0);
            assertThat(view.questionId()).isEqualTo(question.getId());
            assertThat(view.category()).isEqualTo(question.getCategory().name());
            assertThat(view.questionContent()).isEqualTo(question.getContent());
            assertThat(view.answerContent()).isEqualTo(answer.getContent());
            assertThat(view.answerId()).isEqualTo(answer.getId());
            assertThat(view.isAnswered()).isEqualTo(true);
        }

        @Test
        @DisplayName("답변 안한 질문 조회 테스트")
        void findAllQuestionByCategoryWithMemberIdWithoutAnswer() {
            // given
            Member member = Member.fromPhoneNumber("01012345678");
            entityManager.persist(member);
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            entityManager.flush();

            // when
            List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
                question.getCategory().name(), member.getId());

            // then
            assertThat(views).hasSize(1);
            InterviewQuestionView view = views.get(0);
            assertThat(view.questionId()).isEqualTo(question.getId());
            assertThat(view.category()).isEqualTo(question.getCategory().name());
            assertThat(view.questionContent()).isEqualTo(question.getContent());
            assertThat(view.answerContent()).isNull();
            assertThat(view.answerId()).isNull();
            assertThat(view.isAnswered()).isEqualTo(false);
        }

        @Test
        @DisplayName("isPublic이 false인 질문 조회 테스트")
        void findAllQuestionByCategoryWithMemberIdWithoutPublic() {
            // given
            Member member = Member.fromPhoneNumber("01012345678");
            entityManager.persist(member);
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, false);
            entityManager.persist(question);
            entityManager.flush();

            // when
            List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
                question.getCategory().name(), member.getId());

            // then
            assertThat(views).isEmpty();
        }

        @Test
        @DisplayName("다른 카테고리 조회 테스트")
        void findAllQuestionByCategoryWithMemberIdWithDifferentCategory() {
            // given
            Member member = Member.fromPhoneNumber("01012345678");
            entityManager.persist(member);
            InterviewQuestion question = InterviewQuestion.of("질문1", InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            entityManager.flush();

            // when
            List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
                InterviewCategory.SOCIAL.name(), member.getId());

            // then
            assertThat(views).isEmpty();
        }
    }

}