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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, InterviewQuestionQueryRepository.class})
class InterviewQuestionQueryRepositoryTest {

    @Autowired
    private InterviewQuestionQueryRepository interviewQuestionQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Member createMember(String phoneNumber) {
        Member member = Member.fromPhoneNumber(phoneNumber);
        entityManager.persist(member);
        return member;
    }

    private InterviewQuestion createInterviewQuestion(String content, InterviewCategory category, boolean isPublic) {
        InterviewQuestion question = InterviewQuestion.of(content, category, isPublic);
        entityManager.persist(question);
        return question;
    }

    private InterviewAnswer createInterviewAnswer(Long questionId, Long memberId, String content) {
        InterviewAnswer answer = InterviewAnswer.of(questionId, memberId, content);
        entityManager.persist(answer);
        return answer;
    }

    @Nested
    @DisplayName("멤버 인터뷰 질문 조회 테스트")
    class MemberInterviewQuestionTest {

        @Test
        @DisplayName("멤버 인터뷰 질문 조회 파라미터 테스트")
        void findAllQuestionByCategoryWithMemberId() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            InterviewAnswer answer = createInterviewAnswer(question.getId(), member.getId(), "답변1");
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
            assertThat(view.isAnswered()).isTrue();
        }

        @Test
        @DisplayName("답변 안한 질문 조회 테스트")
        void findAllQuestionByCategoryWithMemberIdWithoutAnswer() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
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
            assertThat(view.isAnswered()).isFalse();
        }

        @Test
        @DisplayName("isPublic이 false인 질문 조회 테스트")
        void findAllQuestionByCategoryWithMemberIdWithoutPublic() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, false);
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
            Member member = createMember("01012345678");
            createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            entityManager.flush();

            // when
            List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
                InterviewCategory.SOCIAL.name(), member.getId());

            // then
            assertThat(views).isEmpty();
        }
    }

    @Nested
    @DisplayName("인터뷰 질문 단건 조회 테스트")
    class FindQuestionByIdWithMemberIdTest {

        @Test
        @DisplayName("인터뷰 질문 조회 시 모든 필드가 정확히 매핑되어 DTO 반환")
        void findQuestionByIdWithMemberId() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            InterviewAnswer answer = createInterviewAnswer(question.getId(), member.getId(), "답변1");
            entityManager.flush();

            // when
            InterviewQuestionView view = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question.getId(), member.getId()).orElseThrow();

            // then
            assertThat(view.questionId()).isEqualTo(question.getId());
            assertThat(view.category()).isEqualTo(question.getCategory().name());
            assertThat(view.questionContent()).isEqualTo(question.getContent());
            assertThat(view.answerContent()).isEqualTo(answer.getContent());
            assertThat(view.answerId()).isEqualTo(answer.getId());
            assertThat(view.isAnswered()).isTrue();
        }

        @Test
        @DisplayName("답변 안한 질문 단건 조회 테스트")
        void findQuestionByIdWithMemberIdWithoutAnswer() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            entityManager.flush();

            // when
            InterviewQuestionView view = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question.getId(), member.getId()).orElseThrow();

            // then
            assertThat(view.questionId()).isEqualTo(question.getId());
            assertThat(view.category()).isEqualTo(question.getCategory().name());
            assertThat(view.questionContent()).isEqualTo(question.getContent());
            assertThat(view.answerContent()).isNull();
            assertThat(view.answerId()).isNull();
            assertThat(view.isAnswered()).isFalse();
        }

        @Test
        @DisplayName("다른 멤버의 답변 조회 테스트")
        void findQuestionByIdWithMemberIdWithDifferentMember() {
            // given
            Member member1 = createMember("01012345678");
            Member member2 = createMember("01087654321");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            createInterviewAnswer(question.getId(), member1.getId(), "답변1");
            entityManager.flush();

            // when
            InterviewQuestionView view = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question.getId(), member2.getId()).orElseThrow();

            // then
            assertThat(view.questionId()).isEqualTo(question.getId());
            assertThat(view.isAnswered()).isFalse();
        }

        @Test
        @DisplayName("isPublic이 false인 질문 단건 조회 테스트")
        void findQuestionByIdWithMemberIdWithoutPublic() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, false);
            entityManager.flush();

            // when
            Optional<InterviewQuestionView> optionalView = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question.getId(), member.getId());

            // then
            assertThat(optionalView).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 질문 단건 조회 테스트")
        void findQuestionByIdWithMemberIdNotFound() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            entityManager.flush();

            // when
            InterviewQuestionView view = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question.getId() + 1L, member.getId()).orElse(null);

            // then
            assertThat(view).isNull();
        }

        @Test
        @DisplayName("id가 일치하는 질문 단건 조회 테스트")
        void findQuestionByIdWithMemberIdWithSameId() {
            // given
            Member member = createMember("01012345678");
            InterviewQuestion question1 = createInterviewQuestion("질문1", InterviewCategory.PERSONAL, true);
            InterviewQuestion question2 = createInterviewQuestion("질문2", InterviewCategory.PERSONAL, true);
            entityManager.flush();

            // when
            InterviewQuestionView view = interviewQuestionQueryRepository.findQuestionByIdWithMemberId(
                question1.getId(), member.getId()).orElseThrow();

            // then
            assertThat(view.questionId()).isEqualTo(question1.getId());
        }
    }
}