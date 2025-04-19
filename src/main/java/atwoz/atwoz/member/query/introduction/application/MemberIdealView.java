package atwoz.atwoz.member.query.introduction.application;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record MemberIdealView(
        Integer minAge,
        Integer maxAge,
        List<String> hobbies,
        List<String> regions,
        String religion,
        String smokingStatus,
        String drinkingStatus
) {
    @QueryProjection
    public MemberIdealView {
    }
}
