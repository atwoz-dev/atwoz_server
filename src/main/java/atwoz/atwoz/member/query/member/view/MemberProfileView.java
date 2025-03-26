package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record MemberProfileView(
        String nickname,
        Integer yearOfBirth,
        String gender,
        Integer height,
        String job,
        List<String> hobbies,
        String mbti,
        String region,
        String smokingStatus,
        String drinkingStatus,
        String highestEducation,
        String religion
) {

    @QueryProjection
    public MemberProfileView {
    }
}
