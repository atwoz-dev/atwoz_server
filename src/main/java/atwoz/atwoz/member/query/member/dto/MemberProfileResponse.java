package atwoz.atwoz.member.query.member.dto;

import java.util.List;

public record MemberProfileResponse(
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
}
