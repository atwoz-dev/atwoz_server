package deepple.deepple.interview.command.application.answer;

import deepple.deepple.interview.command.application.answer.exception.InterviewAnswerAccessDeniedException;
import deepple.deepple.interview.command.application.answer.exception.InterviewAnswerAlreadyExistsException;
import deepple.deepple.interview.command.application.answer.exception.InterviewAnswerNotFoundException;
import deepple.deepple.interview.command.application.question.exception.InterviewQuestionIsNotPublicException;
import deepple.deepple.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import deepple.deepple.interview.command.domain.answer.InterviewAnswer;
import deepple.deepple.interview.command.domain.answer.InterviewAnswerCommandRepository;
import deepple.deepple.interview.command.domain.question.InterviewQuestion;
import deepple.deepple.interview.command.domain.question.InterviewQuestionCommandRepository;
import deepple.deepple.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import deepple.deepple.interview.presentation.answer.dto.InterviewAnswerUpdateRequest;
import deepple.deepple.mission.command.application.memberMission.MemberMissionService;
import deepple.deepple.mission.command.domain.mission.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewAnswerService {

    private final InterviewAnswerCommandRepository interviewAnswerCommandRepository;
    private final InterviewQuestionCommandRepository interviewQuestionCommandRepository;
    private final MemberMissionService memberMissionService;

    @Transactional
    public boolean saveAnswer(InterviewAnswerSaveRequest request, Long memberId) {
        validateQuestion(request.interviewQuestionId(), memberId);
        boolean isFirstInterviewAnswer = isFirstInterviewAnswer(memberId);
        createInterviewAnswer(request.interviewQuestionId(), memberId, request.answerContent());
        boolean hasProcessedMission = false;
        if (isFirstInterviewAnswer) {
            hasProcessedMission = memberMissionService.executeMissionsByAction(memberId, ActionType.INTERVIEW.name());
        }
        return hasProcessedMission;
    }

    @Transactional
    public void updateAnswer(Long answerId, InterviewAnswerUpdateRequest request, Long memberId) {
        InterviewAnswer interviewAnswer = getInterviewAnswer(answerId);
        validateAnswer(interviewAnswer, memberId);
        interviewAnswer.updateContent(request.answerContent());
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long memberId) {
        InterviewAnswer interviewAnswer = getInterviewAnswer(answerId);
        validateAnswer(interviewAnswer, memberId);
        interviewAnswerCommandRepository.delete(interviewAnswer);
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
