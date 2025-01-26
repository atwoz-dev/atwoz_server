package atwoz.atwoz.member.application.dto;

import lombok.Builder;

import java.util.List;

@Builder
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
