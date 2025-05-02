package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

public record MemberProfileView(
    String nickname,
    Integer yearOfBirth,
    String gender,
    Integer height,
    String job,
    Set<String> hobbies,
    String mbti,
    String city,
    String district,
    String smokingStatus,
    String drinkingStatus,
    String highestEducation,
    String religion
) {

    @QueryProjection
    public MemberProfileView {
    }
}
