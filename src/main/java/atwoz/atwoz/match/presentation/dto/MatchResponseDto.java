package atwoz.atwoz.match.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record MatchResponseDto(
        Long matchId,
        @NotBlank String responseMessage
) {
}
