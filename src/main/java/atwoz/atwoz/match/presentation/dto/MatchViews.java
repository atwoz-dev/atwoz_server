package atwoz.atwoz.match.presentation.dto;

import atwoz.atwoz.match.query.MatchView;

import java.util.List;

public record MatchViews(List<MatchView> matches, boolean hasMore) {
}
