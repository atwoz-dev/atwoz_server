package atwoz.atwoz.interview.command.application.question;

import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionAlreadyExistsException;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestionCommandRepository;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewQuestionService {

    private final InterviewQuestionCommandRepository interviewQuestionCommandRepository;

    @Transactional
    public void createQuestion(InterviewQuestionSaveRequest request) {
        validateQuestion(request.questionContent());
        InterviewCategory interviewCategory = InterviewQuestionMapper.toInterviewCategory(request.category());
        createQuestion(request.questionContent(), interviewCategory, request.isPublic());
    }

    @Transactional
    public void updateQuestion(Long questionId, InterviewQuestionSaveRequest request) {
        InterviewQuestion interviewQuestion = getInterviewQuestion(questionId);
        InterviewCategory interviewCategory = InterviewQuestionMapper.toInterviewCategory(request.category());
        validateQuestion(interviewQuestion, request.questionContent());
        interviewQuestion.update(request.questionContent(), interviewCategory, request.isPublic());
    }

    private void validateQuestion(String questionContent) {
        if (interviewQuestionCommandRepository.existsByContent(questionContent)) {
            throw new InterviewQuestionAlreadyExistsException();
        }
    }

    private void createQuestion(String questionContent, InterviewCategory interviewCategory, boolean isPublic) {
        InterviewQuestion interviewQuestion = InterviewQuestion.of(questionContent, interviewCategory, isPublic);
        interviewQuestionCommandRepository.save(interviewQuestion);
    }

    private InterviewQuestion getInterviewQuestion(Long questionId) {
        return interviewQuestionCommandRepository.findById(questionId)
                .orElseThrow(() -> new InterviewQuestionNotFoundException());
    }

    private void validateQuestion(InterviewQuestion interviewQuestion, String questionContent) {
        if (interviewQuestion.getContent().equals(questionContent)) {
            return;
        }
        if (interviewQuestionCommandRepository.existsByContent(questionContent)) {
            throw new InterviewQuestionAlreadyExistsException();
        }
    }
}
