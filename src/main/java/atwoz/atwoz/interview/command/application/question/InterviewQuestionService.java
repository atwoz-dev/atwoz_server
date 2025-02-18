package atwoz.atwoz.interview.command.application.question;

import atwoz.atwoz.interview.command.application.answer.InterviewQuestionMapper;
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
    public void saveQuestion(InterviewQuestionSaveRequest request) {
        validateQuestion(request.questionContent());
        InterviewCategory interviewCategory = InterviewQuestionMapper.toInterviewCategory(request.category());
        createQuestion(request.questionContent(), interviewCategory, request.isPublic());
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
}
