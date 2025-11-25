package atwoz.atwoz.interview.presentation.answer;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.answer.InterviewAnswerService;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerCreateResponse;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인터뷰 답변 관리 API")
@RestController
@RequestMapping("/interview/answer")
@RequiredArgsConstructor
public class InterviewAnswerController {

    private final InterviewAnswerService interviewAnswerService;

    @Operation(summary = "인터뷰 답변 등록 API")
    @PostMapping
    public ResponseEntity<BaseResponse<InterviewAnswerCreateResponse>> saveAnswer(
        @Valid @RequestBody InterviewAnswerSaveRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        boolean hasProcessedMission = interviewAnswerService.saveAnswer(request, authContext.getId());
        InterviewAnswerCreateResponse response = new InterviewAnswerCreateResponse(hasProcessedMission);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @Operation(summary = "인터뷰 답변 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateAnswer(
        @PathVariable Long id,
        @Valid @RequestBody InterviewAnswerUpdateRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        interviewAnswerService.updateAnswer(id, request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "인터뷰 답변 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAnswer(
        @PathVariable Long id,
        @AuthPrincipal AuthContext authContext
    ) {
        interviewAnswerService.deleteAnswer(id, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
