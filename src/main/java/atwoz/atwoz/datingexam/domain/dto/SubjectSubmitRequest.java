package atwoz.atwoz.datingexam.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubjectSubmitRequest(
    @NotNull
    Long subjectId,
    @NotNull
    @NotEmpty
    List<AnswerSubmitRequest> answers
) {
}
