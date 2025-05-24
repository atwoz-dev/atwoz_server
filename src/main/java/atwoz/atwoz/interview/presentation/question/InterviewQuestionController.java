package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.command.application.question.exception.InterviewQuestionNotFoundException;
import atwoz.atwoz.interview.query.condition.InterviewQuestionSearchCondition;
import atwoz.atwoz.interview.query.question.InterviewQuestionQueryRepository;
import atwoz.atwoz.interview.query.question.view.InterviewQuestionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "인터뷰 질문 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/interview/question")
public class InterviewQuestionController {
    private final InterviewQuestionQueryRepository interviewQuestionQueryRepository;

    @Operation(summary = "인터뷰 질문 목록 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse<List<InterviewQuestionView>>> getQuestionAllByCategory(
        @ModelAttribute InterviewQuestionSearchCondition interviewQuestionSearchCondition,
        @AuthPrincipal AuthContext authContext
    ) {
        List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(
            interviewQuestionSearchCondition, authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, views));
    }

    @Operation(summary = "인터뷰 질문 상세 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InterviewQuestionView>> getQuestionById(
        @PathVariable Long id,
        @AuthPrincipal AuthContext authContext
    ) {
        InterviewQuestionView view = interviewQuestionQueryRepository
            .findQuestionByIdWithMemberId(id, authContext.getId())
            .orElseThrow(InterviewQuestionNotFoundException::new);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, view));
    }
}
