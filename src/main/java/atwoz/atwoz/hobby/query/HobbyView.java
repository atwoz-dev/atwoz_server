package atwoz.atwoz.hobby.query;

import com.querydsl.core.annotations.QueryProjection;

public record HobbyView(
        Long id,
        String name
) {
    @QueryProjection
    public HobbyView {
    }
}
