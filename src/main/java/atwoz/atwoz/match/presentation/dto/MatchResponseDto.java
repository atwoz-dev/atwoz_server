package atwoz.atwoz.match.presentation.dto;

import atwoz.atwoz.match.command.domain.match.MatchContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MatchResponseDto(
    @NotBlank String responseMessage,
    @Schema(implementation = MatchContactType.class)
    String contactType
) {
}
