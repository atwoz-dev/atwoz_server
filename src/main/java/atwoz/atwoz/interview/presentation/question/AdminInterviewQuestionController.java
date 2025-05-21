package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.InterviewQuestionService;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.presentation.question.dto.InterviewQuestionSaveRequest;
import atwoz.atwoz.interview.query.question.AdminInterviewQuestionQueryRepository;
import atwoz.atwoz.interview.query.question.view.AdminInterviewQuestionView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/interview/question")
public class AdminInterviewQuestionController {
    private final InterviewQuestionService interviewQuestionService;
    private final AdminInterviewQuestionQueryRepository adminInterviewQuestionQueryRepository;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createQuestion(
        @Valid @RequestBody InterviewQuestionSaveRequest request
    ) {
        interviewQuestionService.createQuestion(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateQuestion(
        @PathVariable Long id,
        @Valid @RequestBody InterviewQuestionSaveRequest request
    ) {
        interviewQuestionService.updateQuestion(id, request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdminInterviewQuestionView>>> getQuestionPage(
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<AdminInterviewQuestionView> views = adminInterviewQuestionQueryRepository.findAdminInterviewQuestionPage(
            pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, views));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AdminInterviewQuestionView>> getQuestionById(
        @PathVariable Long id
    ) {
        AdminInterviewQuestionView view = adminInterviewQuestionQueryRepository.findAdminInterviewQuestionById(id)
            .orElseThrow(InterviewQuestionNotFoundException::new);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, view));
    }
}
