package atwoz.atwoz.admin.query.selfintroduction;

import com.querydsl.core.annotations.QueryProjection;

public record AdminSelfIntroductionView(
    long selfIntroductionId,
    String nickname,
    String gender,
    boolean isOpened,
    String content,
    String createdDate,
    String updatedDate,
    String deletedDate
) {
    @QueryProjection
    public AdminSelfIntroductionView {
    }
}
