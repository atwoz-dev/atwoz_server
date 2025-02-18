package atwoz.atwoz.interview.command.application.question;

import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionAlreadyExistsException;
import atwoz.atwoz.interview.command.application.question.exception.InvalidInterviewCategoryException;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewQuestionServiceTest {

    @Mock
    private InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @InjectMocks
    private InterviewQuestionService interviewQuestionService;

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
        assertThatThrownBy(() -> interviewQuestionService.saveQuestion(request))
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
        assertThatThrownBy(() -> interviewQuestionService.saveQuestion(request))
                .isInstanceOf(InvalidInterviewCategoryException.class);
    }

    @Test
    @DisplayName("인터뷰 질문을 저장한다.")
    void saveInterviewQuestion() {
        // given
        String content = "content";
        String category = InterviewCategory.PERSONAL.name();
        boolean isPublic = true;
        InterviewQuestionSaveRequest request = new InterviewQuestionSaveRequest(content, category, isPublic);

        when(interviewQuestionCommandRepository.existsByContent(content)).thenReturn(false);

        // when
        interviewQuestionService.saveQuestion(request);

        // then
        verify(interviewQuestionCommandRepository).save(any(InterviewQuestion.class));
    }
}