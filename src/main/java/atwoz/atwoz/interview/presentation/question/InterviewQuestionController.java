package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.InterviewQuestionService;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @PostMapping("/admin/interview/questions")
    public ResponseEntity<BaseResponse<Void>> saveQuestion(InterviewQuestionSaveRequest request) {
        interviewQuestionService.saveQuestion(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
