package atwoz.atwoz.interview.presentation.question;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.interview.query.question.InterviewQuestionQueryRepository;
import atwoz.atwoz.interview.query.question.view.InterviewQuestionView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interview/question")
public class InterviewQuestionController {
    private final InterviewQuestionQueryRepository interviewQuestionQueryRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<List<InterviewQuestionView>>> getQuestionAllByCategory(
            @RequestParam String category,
            @AuthPrincipal AuthContext authContext
    ) {
        List<InterviewQuestionView> views = interviewQuestionQueryRepository.findAllQuestionByCategoryWithMemberId(category, authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, views));
    }
}
