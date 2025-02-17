package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record ScreeningDetailView(
    Long screeningId,
    Long memberId,
    String screeningStatus,
    String rejectionReason,
    String nickname,
    Integer age,
    String gender,
    String joinedDate,
    List<String> profileImageUrls
    // TODO: interviews
) {
    @QueryProjection
    public ScreeningDetailView {
    }
}
