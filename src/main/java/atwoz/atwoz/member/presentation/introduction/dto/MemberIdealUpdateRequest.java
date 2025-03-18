package atwoz.atwoz.member.presentation.introduction.dto;

import java.util.Set;

public record MemberIdealUpdateRequest(
        Integer minAge,
        Integer maxAge,
        String religion,
        String region,
        String smokingStatus,
        String drinkingStatus,
        Set<Long> hobbyIds
) {
}
