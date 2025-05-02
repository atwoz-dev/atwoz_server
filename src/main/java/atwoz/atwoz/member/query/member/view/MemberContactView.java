package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

public record MemberContactView(
    String phoneNumber,
    String kakaoId,
    String primaryContactType
) {
    @QueryProjection
    public MemberContactView {
    }
}
