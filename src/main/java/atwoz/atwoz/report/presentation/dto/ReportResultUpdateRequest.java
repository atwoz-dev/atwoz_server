package atwoz.atwoz.report.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportResultUpdateRequest(
    @NotNull(message = "버전을 입력해주세요.")
    Long version,
    @NotBlank(message = "신고 결과를 입력해주세요.")
    String result
) {
}
