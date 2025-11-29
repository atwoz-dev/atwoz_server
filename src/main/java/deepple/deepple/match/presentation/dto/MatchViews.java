package deepple.deepple.match.presentation.dto;

import deepple.deepple.match.query.MatchView;

import java.util.List;

public record MatchViews(List<MatchView> matches, boolean hasMore) {
}
