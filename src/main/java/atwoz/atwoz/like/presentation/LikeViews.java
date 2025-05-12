package atwoz.atwoz.like.presentation;

import atwoz.atwoz.like.query.LikeView;

import java.util.List;

public record LikeViews(List<LikeView> likes, boolean hasMore) {
}
