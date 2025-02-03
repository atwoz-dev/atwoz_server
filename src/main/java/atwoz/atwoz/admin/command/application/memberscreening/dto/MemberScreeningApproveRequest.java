package atwoz.atwoz.admin.command.application.memberscreening.dto;

import jakarta.validation.constraints.NotNull;

public record MemberScreeningApproveRequest(
        @NotNull(message = "멤버 id는 null일 수 없습니다.")
        Long memberId,

        @NotNull(message = "version은 null일 수 없습니다.")
        Long version
) {
}
