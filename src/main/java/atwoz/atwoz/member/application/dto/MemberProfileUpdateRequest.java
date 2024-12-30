package atwoz.atwoz.member.application.dto;

import java.util.List;

public record MemberProfileUpdateRequest(
        String nickName,
        String gender,
        Integer age,
        Integer height,
        Long jobId,
        String region,
        String lastEducation,
        String mbti,
        String smokingStatus,
        String drinkingStatus,
        String religionStatus,
        List<Long> hobbyIds
) {
}
