package atwoz.atwoz.member.presentation.introduction.dto;

import java.util.Set;

public record MemberIdealUpdateRequest(
        Integer minAge,
        Integer maxAge,
        String region,
        String religion,
        String smokingStatus,
        String drinkingStatus,
        Set<Long> hobbyIds
) {
}
