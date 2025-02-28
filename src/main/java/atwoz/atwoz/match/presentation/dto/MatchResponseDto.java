package atwoz.atwoz.match.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record MatchResponseDto(
        @NotBlank String responseMessage
) {
}
