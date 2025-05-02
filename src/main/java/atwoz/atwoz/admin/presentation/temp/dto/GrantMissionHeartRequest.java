package atwoz.atwoz.admin.presentation.temp.dto;

import jakarta.validation.constraints.Min;
import lombok.NonNull;

public record GrantMissionHeartRequest(
    @NonNull
    Long memberId,
    @Min(value = 1, message = "하트 지급은 최소 1개 이상으로 설정")
    Long heartAmount
) {
}
