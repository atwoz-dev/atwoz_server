package atwoz.atwoz.member.query.member.view;

import java.util.Set;

public record BasicMemberInfo(
    Long id,
    String nickname,
    String profileImageUrl,
    Integer yearOfBirth,
    String gender,
    Integer height,
    String job,
    Set<String> hobbies,
    String mbti,
    String city,
    String smokingStatus,
    String drinkingStatus,
    String highestEducation,
    String religion,
    String like
) {
}
