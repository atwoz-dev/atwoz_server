package atwoz.atwoz.interview.command.application.answer;

import atwoz.atwoz.interview.command.application.answer.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswerCommandRepository;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewAnswerServiceTest {
    @Mock
    private InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @Mock
    private InterviewAnswerCommandRepository interviewAnswerCommandRepository;

    @InjectMocks
    private InterviewAnswerService interviewAnswerService;

    @Test
    @DisplayName("인터뷰 질문이 존재하지 않으면 예외를 던진다.")
    void throwsExceptionWhenInterviewQuestionDoesNotExist() {
        // given
        Long interviewQuestionId = 1L;
        String answerContent = "content";
        InterviewAnswerSaveRequest request = new InterviewAnswerSaveRequest(interviewQuestionId, answerContent);
        Long memberId = 2L;

        when(interviewQuestionCommandRepository.existsById(interviewQuestionId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> interviewAnswerService.saveAnswer(request, memberId))
                .isInstanceOf(InterviewQuestionNotFoundException.class);
    }

    @Test
    @DisplayName("다른 인터뷰 답변이 존재하지 않으면 submitFirstInterviewAnswer를 호출한다.")
    void callsSubmitFirstInterviewAnswerWhenOtherInterviewAnswerDoesNotExist() {
        // given
        Long interviewQuestionId = 1L;
        String answerContent = "content";
        InterviewAnswerSaveRequest request = new InterviewAnswerSaveRequest(interviewQuestionId, answerContent);
        Long memberId = 2L;

        when(interviewQuestionCommandRepository.existsById(interviewQuestionId)).thenReturn(true);
        when(interviewAnswerCommandRepository.existsByMemberId(memberId)).thenReturn(false);

        InterviewAnswer interviewAnswer = mock(InterviewAnswer.class);
        try (MockedStatic<InterviewAnswer> mockedStatic = mockStatic(InterviewAnswer.class)) {
            mockedStatic.when(() -> InterviewAnswer.of(eq(interviewQuestionId), eq(memberId), eq(answerContent)))
                    .thenReturn(interviewAnswer);

            // when
            interviewAnswerService.saveAnswer(request, memberId);

            // then
            verify(interviewAnswer, times(1)).submitFirstInterviewAnswer();
        }
        verify(interviewAnswerCommandRepository).save(interviewAnswer);

    }

    @Test
    @DisplayName("다른 인터뷰 답변이 존재하면 submitFirstInterviewAnswer를 호출하지 않는다.")
    void doesNotCallSubmitFirstInterviewAnswerWhenOtherInterviewAnswerExists() {
        // given
        Long interviewQuestionId = 1L;
        String answerContent = "content";
        InterviewAnswerSaveRequest request = new InterviewAnswerSaveRequest(interviewQuestionId, answerContent);
        Long memberId = 2L;

        when(interviewQuestionCommandRepository.existsById(interviewQuestionId)).thenReturn(true);
        when(interviewAnswerCommandRepository.existsByMemberId(memberId)).thenReturn(true);

        InterviewAnswer interviewAnswer = mock(InterviewAnswer.class);
        try (MockedStatic<InterviewAnswer> mockedStatic = mockStatic(InterviewAnswer.class)) {
            mockedStatic.when(() -> InterviewAnswer.of(eq(interviewQuestionId), eq(memberId), eq(answerContent)))
                    .thenReturn(interviewAnswer);

            // when
            interviewAnswerService.saveAnswer(request, memberId);

            // then
            verify(interviewAnswer, never()).submitFirstInterviewAnswer();
        }
        verify(interviewAnswerCommandRepository).save(interviewAnswer);
    }

}