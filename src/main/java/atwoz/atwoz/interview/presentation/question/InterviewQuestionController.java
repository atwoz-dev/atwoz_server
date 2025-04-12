package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.InterviewQuestionService;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import atwoz.atwoz.interview.query.question.InterviewQuestionQueryRepository;
import atwoz.atwoz.interview.query.question.view.InterviewQuestionView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;
    private final InterviewQuestionQueryRepository interviewQuestionQueryRepository;

    @PostMapping("/admin/interview/question")
    public ResponseEntity<BaseResponse<Void>> createQuestion(@Valid @RequestBody InterviewQuestionSaveRequest request) {
        interviewQuestionService.createQuestion(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/admin/interview/question/{id}")
    public ResponseEntity<BaseResponse<Void>> updateQuestion(@PathVariable Long id, @Valid @RequestBody InterviewQuestionSaveRequest request) {
        interviewQuestionService.updateQuestion(id, request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/interview/question")
    public ResponseEntity<BaseResponse<List<InterviewQuestionView>>> getQuestionAllByCategory(
            @RequestParam String category,
            @AuthPrincipal AuthContext authContext
    ) {
        List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(category, authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, views));
    }
}
