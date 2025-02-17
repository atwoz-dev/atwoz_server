package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record ScreeningDetailView(
        long screeningId,
        long memberId,
    String screeningStatus,
    String rejectionReason,
    String nickname,
        int age,
    String gender,
    String joinedDate,
        List<ProfileImageView> profileImages
    // TODO: interviews
) {
    @QueryProjection
    public ScreeningDetailView {
    }
}
