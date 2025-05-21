package atwoz.atwoz.interview.presentation.answer;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.answer.InterviewAnswerService;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview/answer")
@RequiredArgsConstructor
public class InterviewAnswerController {

    private final InterviewAnswerService interviewAnswerService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> saveAnswer(@Valid @RequestBody InterviewAnswerSaveRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        interviewAnswerService.saveAnswer(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateAnswer(
        @PathVariable Long id,
        @Valid @RequestBody InterviewAnswerUpdateRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        interviewAnswerService.updateAnswer(id, request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
