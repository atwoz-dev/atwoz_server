package atwoz.atwoz.interview.presentation.answer;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.interview.command.application.answer.InterviewAnswerService;
import atwoz.atwoz.interview.presentation.answer.dto.InterviewAnswerSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/answer")
@RequiredArgsConstructor
public class InterviewAnswerController {

    private final InterviewAnswerService interviewAnswerService;

    @PostMapping
    public void saveAnswer(@Valid @RequestBody InterviewAnswerSaveRequest request,
        @AuthPrincipal AuthContext authContext) {
        interviewAnswerService.saveAnswer(request, authContext.getId());
    }
}
