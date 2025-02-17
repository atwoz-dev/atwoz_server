package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

public record MemberProfileView(
        String nickname,
        Integer age,
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
