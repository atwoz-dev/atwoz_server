package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.InterviewQuestionService;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @PostMapping("/admin/interview/question")
    public ResponseEntity<BaseResponse<Void>> saveQuestion(@Valid @RequestBody InterviewQuestionSaveRequest request) {
        interviewQuestionService.saveQuestion(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/admin/interview/question/{id}")
    public ResponseEntity<BaseResponse<Void>> updateQuestion(@PathVariable Long id, @Valid @RequestBody InterviewQuestionSaveRequest request) {
        interviewQuestionService.updateQuestion(id, request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
