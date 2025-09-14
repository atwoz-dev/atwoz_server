package atwoz.atwoz.block.adapter.webapi.dto;

import jakarta.validation.constraints.NotNull;

public record BlockCreateRequest(
    @NotNull
    Long blockedId
) {
}
