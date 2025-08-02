package atwoz.atwoz.datingexam.domain.dto;

import jakarta.validation.constraints.NotNull;

public record AnswerSubmitRequest(
    @NotNull
    Long questionId,
    @NotNull
    Long answerId
) {
}
