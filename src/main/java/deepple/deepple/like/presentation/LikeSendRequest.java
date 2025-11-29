package deepple.deepple.like.presentation;

import deepple.deepple.like.command.domain.LikeLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LikeSendRequest(
    long receiverId,
    @Schema(implementation = LikeLevel.class)
    @NotNull LikeLevelRequest likeLevel
) {
}
