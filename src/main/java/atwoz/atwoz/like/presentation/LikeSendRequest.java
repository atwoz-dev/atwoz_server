package atwoz.atwoz.like.presentation;

import jakarta.validation.constraints.NotNull;

public record LikeSendRequest(long receiverId, @NotNull LikeLevelRequest likeLevel) {
}
