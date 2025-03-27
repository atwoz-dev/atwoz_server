package atwoz.atwoz.member.presentation.member.dto;

import java.util.List;

public record BasicInfo(
        Long id,
        String nickname,
        String profileImageUrl,
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
        String religion,
        String like
) {
}
