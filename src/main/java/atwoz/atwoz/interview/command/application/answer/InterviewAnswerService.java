package atwoz.atwoz.interview.command.application.answer;

import atwoz.atwoz.interview.command.application.answer.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswerCommandRepository;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewAnswerService {

    private final InterviewAnswerCommandRepository interviewAnswerCommandRepository;
    private final InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @Transactional
    public void saveAnswer(Long memberId, Long questionId, String content) {
        validateQuestion(questionId);
        boolean hasInterviewAnswer = !interviewAnswerCommandRepository.existsByMemberId(memberId);
        InterviewAnswer interviewAnswer = createInterviewAnswer(questionId, memberId, content);
        interviewAnswer.submit(hasInterviewAnswer);
    }

    private void validateQuestion(Long questionId) {
        if (!interviewQuestionCommandRepository.existsById(questionId)) {
            throw new InterviewQuestionNotFoundException();
        }
    }

    private InterviewAnswer createInterviewAnswer(Long questionId, Long memberId, String content) {
        InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, content);
        return interviewAnswerCommandRepository.save(interviewAnswer);
    }
}
