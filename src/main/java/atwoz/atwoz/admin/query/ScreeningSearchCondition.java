package atwoz.atwoz.admin.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ScreeningSearchCondition(
        @Schema(
                description = "심사 상태",
                allowableValues = {"PENDING", "APPROVED", "REJECTED"},
                example = "PENDING"
        )
        String screeningStatus,

        String nickname,

        @Pattern(regexp = "^010\\d{8}$", message = "올바르지 않은 휴대폰 번호 형식입니다.")
        String phoneNumber,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
}
