package atwoz.atwoz.member.presentation.member.dto;

import java.util.Set;

public record MemberProfileUpdateRequest(
        String nickName,
        String gender,
        Integer yearOfBirth,
        Integer height,
        Long jobId,
        String region,
        String highestEducation,
        String mbti,
        String smokingStatus,
        String drinkingStatus,
        String religionStatus,
        Set<Long> hobbyIds
) {
}
