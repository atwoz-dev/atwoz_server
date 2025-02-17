package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileImageView(
        String imageUrl,
        int order,
        boolean isPrimary
) {
    @QueryProjection
    public ProfileImageView {
    }
}
