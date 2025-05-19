package atwoz.atwoz.report.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequest(
    @NotNull(message = "신고 대상 아이디를 입력해주세요.")
    Long reporteeId,

    @NotBlank(message = "신고 사유를 입력해주세요.")
    String reason,

    @NotNull(message = "신고 내용을 입력해주세요.")
    String content
) {
}
