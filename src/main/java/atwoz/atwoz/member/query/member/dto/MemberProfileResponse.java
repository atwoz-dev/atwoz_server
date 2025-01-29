package atwoz.atwoz.member.query.member.dto;

import lombok.Builder;

@Builder
public record MemberProfileResponse(
        String nickname,
        Integer age,
        String gender,
        Integer height,
        String job,
        Object hobbies,
        String mbti,
        String region,
        String smokingStatus,
        String drinkingStatus,
        String highestEducation,
        String religion
) {
}
