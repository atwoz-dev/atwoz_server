package atwoz.atwoz.admin.command.application.memberscreening.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberScreeningRejectRequest(
        @NotNull(message = "멤버 id는 null일 수 없습니다.")
        Long memberId,

        @NotBlank(message = "반려 사유를 선택해주세요.")
        String rejectionReason,

        @NotNull(message = "version은 null일 수 없습니다.")
        Long version
) {
}
