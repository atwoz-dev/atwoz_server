package atwoz.atwoz.like.presentation;

import atwoz.atwoz.like.command.domain.LikeLevel;
import jakarta.validation.constraints.NotNull;

public record LikeSendRequest(long receiverId, @NotNull LikeLevel likeLevel) {
}
