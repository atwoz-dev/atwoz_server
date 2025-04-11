package atwoz.atwoz.interview.command.application.question;

import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionAlreadyExistsException;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import atwoz.atwoz.interview.command.domain.question.exception.InvalidInterviewCategoryException;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewQuestionServiceTest {

    @Mock
    private InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @InjectMocks
    private InterviewQuestionService interviewQuestionService;

    @Nested
    @DisplayName("createQuestion 메서드 테스트")
    class createQuestionTest {
        @Test
        @DisplayName("인터뷰 질문 내용이 이미 존재하면 예외를 던진다.")
        void throwsExceptionWhenInterviewQuestionContentAlreadyExists() {
            // given
            String content = "content";
            String category = InterviewCategory.PERSONAL.name();
            boolean isPublic = true;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(content, category, isPublic);

            when(interviewQuestionCommandRepository.existsByContent(content)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> interviewQuestionService.createQuestion(request))
                    .isInstanceOf(InterviewQuestionAlreadyExistsException.class);
        }

        @Test
        @DisplayName("인터뷰 카테고리가 존재하지 않으면 예외를 던진다.")
        void throwsExceptionWhenInterviewCategoryDoesNotExist() {
            // given
            String content = "content";
            String category = "notExist";
            boolean isPublic = true;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(content, category, isPublic);

            when(interviewQuestionCommandRepository.existsByContent(content)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> interviewQuestionService.createQuestion(request))
                    .isInstanceOf(InvalidInterviewCategoryException.class);
        }

        @Test
        @DisplayName("인터뷰 질문을 생성한다.")
        void createInterviewQuestion() {
            // given
            String content = "content";
            String category = InterviewCategory.PERSONAL.name();
            boolean isPublic = true;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(content, category, isPublic);

            when(interviewQuestionCommandRepository.existsByContent(content)).thenReturn(false);

            // when
            interviewQuestionService.createQuestion(request);

            // then
            verify(interviewQuestionCommandRepository).save(any(InterviewQuestion.class));
        }
    }

    @Nested
    @DisplayName("updateQuestion 메서드 테스트")
    class updateQuestionTest {

        @Test
        @DisplayName("인터뷰 질문이 존재하지 않으면 예외를 던진다.")
        void throwsExceptionWhenInterviewQuestionDoesNotExist() {
            // given
            Long questionId = 1L;
            String content = "content";
            String category = InterviewCategory.PERSONAL.name();
            boolean isPublic = true;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(content, category, isPublic);

            when(interviewQuestionCommandRepository.findById(questionId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interviewQuestionService.updateQuestion(questionId, request))
                    .isInstanceOf(InterviewQuestionNotFoundException.class);
        }

        @Test
        @DisplayName("업데이트 하려는 인터뷰 카테고리가 존재하지 않으면 예외를 던진다.")
        void throwsExceptionWhenInterviewCategoryDoesNotExist() {
            // given
            Long questionId = 1L;
            InterviewQuestion interviewQuestion = mock(InterviewQuestion.class);

            String updatedContent = "updated content";
            String updatedCategory = "notExist";
            boolean updatedIsPublic = false;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(updatedContent, updatedCategory, updatedIsPublic);

            when(interviewQuestionCommandRepository.findById(questionId)).thenReturn(Optional.of(interviewQuestion));

            // when & then
            assertThatThrownBy(() -> interviewQuestionService.updateQuestion(questionId, request))
                    .isInstanceOf(InvalidInterviewCategoryException.class);
        }

        @Test
        @DisplayName("업데이트 하려는 인터뷰 질문 내용이 기존 질문 내용과 다르고, 해당 내용의 질문이 이미 존재하면 예외를 던진다.")
        void throwsExceptionWhenInterviewQuestionContentAlreadyExists() {
            // given
            Long questionId = 1L;
            String content = "content";
            InterviewQuestion interviewQuestion = mock(InterviewQuestion.class);
            when(interviewQuestion.getContent()).thenReturn(content);

            String updatedContent = "updated content";
            String updatedCategory = InterviewCategory.SOCIAL.name();
            boolean updatedIsPublic = false;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(updatedContent, updatedCategory, updatedIsPublic);

            when(interviewQuestionCommandRepository.findById(questionId)).thenReturn(Optional.of(interviewQuestion));
            when(interviewQuestionCommandRepository.existsByContent(updatedContent)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> interviewQuestionService.updateQuestion(questionId, request))
                    .isInstanceOf(InterviewQuestionAlreadyExistsException.class);
        }

        @Test
        @DisplayName("인터뷰 질문을 업데이트한다.")
        void updateInterviewQuestion() {
            // given
            Long questionId = 1L;
            String content = "content";
            InterviewQuestion interviewQuestion = mock(InterviewQuestion.class);
            when(interviewQuestion.getContent()).thenReturn(content);

            String updatedContent = "updated content";
            String updatedCategory = InterviewCategory.SOCIAL.name();
            boolean updatedIsPublic = false;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(updatedContent, updatedCategory, updatedIsPublic);

            when(interviewQuestionCommandRepository.findById(questionId)).thenReturn(Optional.of(interviewQuestion));
            when(interviewQuestionCommandRepository.existsByContent(updatedContent)).thenReturn(false);

            // when
            interviewQuestionService.updateQuestion(questionId, request);

            // then
            verify(interviewQuestion).update(updatedContent, InterviewCategory.SOCIAL, false);
        }

        @Test
        @DisplayName("인터뷰 질문 내용이 기존 질문 내용과 동일하면 업데이트한다.")
        void updateInterviewQuestionWhenContentIsSame() {
            // given
            Long questionId = 1L;
            String content = "content";
            InterviewQuestion interviewQuestion = mock(InterviewQuestion.class);
            when(interviewQuestion.getContent()).thenReturn(content);

            String updatedContent = "content";
            String updatedCategory = InterviewCategory.SOCIAL.name();
            boolean updatedIsPublic = false;
            InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(updatedContent, updatedCategory, updatedIsPublic);

            when(interviewQuestionCommandRepository.findById(questionId)).thenReturn(Optional.of(interviewQuestion));

            // when
            interviewQuestionService.updateQuestion(questionId, request);

            // then
            verify(interviewQuestion).update(updatedContent, InterviewCategory.SOCIAL, false);
        }
    }

}