package atwoz.atwoz.admin.query.screening;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningDetailProfileImageView(
    String imageUrl,
    int order,
    boolean isPrimary
) {
    @QueryProjection
    public ScreeningDetailProfileImageView {
    }
}
