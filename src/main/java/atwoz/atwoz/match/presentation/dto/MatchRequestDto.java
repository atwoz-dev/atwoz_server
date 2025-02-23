package atwoz.atwoz.match.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record MatchRequestDto(
        @NotNull(message = "메세지를 입력해주세요.") String requestMessage
) {
}
