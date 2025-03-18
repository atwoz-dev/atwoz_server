package atwoz.atwoz.member.query.introduction.application;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record MemberIdealView(
        Integer minAge,
        Integer maxAge,
        List<String> hobbies,
        String region,
        String religion,
        String smokingStatus,
        String drinkingStatus
) {
    @QueryProjection
    public MemberIdealView {
    }
}
