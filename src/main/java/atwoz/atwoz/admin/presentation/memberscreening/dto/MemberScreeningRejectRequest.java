package atwoz.atwoz.admin.presentation.memberscreening.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberScreeningRejectRequest(
        @NotNull(message = "멤버 id는 null일 수 없습니다.")
        Long memberId,

        @Schema(
                description = "반려 사유",
                allowableValues = {"STOLEN_IMAGE", "INAPPROPRIATE_IMAGE", "EXPLICIT_CONTENT", "OFFENSIVE_LANGUAGE", "CONTACT_IN_PROFILE"},
                example = "STOLEN_IMAGE"
        )
        @NotBlank(message = "반려 사유를 선택해주세요.")
        String rejectionReason
) {
}
