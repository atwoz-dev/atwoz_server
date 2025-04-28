package atwoz.atwoz.job.query;

import com.querydsl.core.annotations.QueryProjection;

public record JobView(
        Long id,
        String name
) {
    @QueryProjection
    public JobView {
    }
}
