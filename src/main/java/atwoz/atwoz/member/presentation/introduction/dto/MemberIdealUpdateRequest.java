package atwoz.atwoz.member.presentation.introduction.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record MemberIdealUpdateRequest(
        @NotNull(message = "최소 나이를 입력해주세요.")
        @Min(value = 20, message = "최소 나이는 20보다 작을 수 없습니다.")
        @Max(value = 46, message = "최소 나이는 46보다 작을 수 없습니다.")
        Integer minAge,
        @NotNull(message = "최대 나이를 입력해주세요.")
        @Min(value = 20, message = "최대 나이는 20보다 작을 수 없습니다.")
        @Max(value = 46, message = "최대 나이는 46보다 작을 수 없습니다.")
        Integer maxAge,
        @NotNull(message = "지역을 입력해주세요.")
        Set<String> cities,
        String religion,
        String smokingStatus,
        String drinkingStatus,
        @NotNull(message = "취미를 입력해주세요.")
        Set<Long> hobbyIds
) {
}
