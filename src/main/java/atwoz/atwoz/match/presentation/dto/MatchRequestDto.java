package atwoz.atwoz.match.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatchRequestDto(
    @NotNull(message = "응답자 아이디를 입력해주세요.") Long responderId,
    @NotBlank(message = "메세지를 입력해주세요.") String requestMessage
) {
}
