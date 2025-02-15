package atwoz.atwoz.admin.presentation.memberscreening.dto;

import jakarta.validation.constraints.NotNull;

public record MemberScreeningApproveRequest(
        @NotNull(message = "멤버 id는 null일 수 없습니다.")
        Long memberId
) {
}
