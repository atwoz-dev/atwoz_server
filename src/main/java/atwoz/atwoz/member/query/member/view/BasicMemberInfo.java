package atwoz.atwoz.member.query.member.view;

import java.util.List;

public record BasicMemberInfo(
        Long id,
        String nickname,
        String profileImageUrl,
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
        String religion,
        String like
) {
}
