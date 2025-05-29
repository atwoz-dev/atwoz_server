package atwoz.atwoz.interview.command.application.answer;

import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerAccessDeniedException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerAlreadyExistsException;
import atwoz.atwoz.interview.command.application.answer.exception.InterviewAnswerNotFoundException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionIsNotPublicException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswerCommandRepository;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewAnswerService {

    private final InterviewAnswerCommandRepository interviewAnswerCommandRepository;
    private final InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @Transactional
    public void saveAnswer(InterviewAnswerSaveRequest request, Long memberId) {
        validateQuestion(request.interviewQuestionId(), memberId);
        createInterviewAnswer(request.interviewQuestionId(), memberId, request.answerContent());
    }

    @Transactional
    public void updateAnswer(Long answerId, InterviewAnswerUpdateRequest request, Long memberId) {
        InterviewAnswer interviewAnswer = getInterviewAnswer(answerId);
        validateAnswer(interviewAnswer, memberId);
        interviewAnswer.updateContent(request.answerContent());
    }

    private void validateQuestion(Long questionId, Long memberId) {
        InterviewQuestion interviewQuestion = interviewQuestionCommandRepository.findById(questionId)
            .orElseThrow(InterviewQuestionNotFoundException::new);
        if (!interviewQuestion.isPublic()) {
            throw new InterviewQuestionIsNotPublicException();
        }
        if (interviewAnswerCommandRepository.existsByQuestionIdAndMemberId(questionId, memberId)) {
            throw new InterviewAnswerAlreadyExistsException();
        }
    }

    private InterviewAnswer createInterviewAnswer(Long questionId, Long memberId, String content) {
        InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, content);
        if (isFirstInterviewAnswer(memberId)) {
            interviewAnswer.submitFirstInterviewAnswer();
        }
        return interviewAnswerCommandRepository.save(interviewAnswer);
    }

    private boolean isFirstInterviewAnswer(Long memberId) {
        return !interviewAnswerCommandRepository.existsByMemberId(memberId);
    }

    private InterviewAnswer getInterviewAnswer(Long id) {
        return interviewAnswerCommandRepository.findById(id)
            .orElseThrow(InterviewAnswerNotFoundException::new);
    }

    private void validateAnswer(InterviewAnswer interviewAnswer, Long memberId) {
        if (!interviewAnswer.isAnsweredBy(memberId)) {
            throw new InterviewAnswerAccessDeniedException();
        }
    }
}
