package deepple.deepple.like.presentation;

import deepple.deepple.like.query.LikeView;

import java.util.List;

public record LikeViews(List<LikeView> likes, boolean hasMore) {
}
