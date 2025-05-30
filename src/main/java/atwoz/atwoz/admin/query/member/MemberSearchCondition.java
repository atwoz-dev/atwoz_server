package atwoz.atwoz.admin.query.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MemberSearchCondition(
    @Schema(
        description = "활동 상태",
        allowableValues = {"ACTIVE", "BANNED", "SUSPENDED", "DORMANT"},
        example = "ACTIVE"
    )
    String activityStatus,

    @Schema(
        description = "성별",
        allowableValues = {"MALE", "FEMALE"},
        example = "MALE"
    )
    String gender,

    @Schema(
        description = "등급",
        allowableValues = {"DIAMOND", "GOLD", "SILVER"},
        example = "DIAMOND"
    )
    String grade,

    String nickname,

    @Pattern(regexp = "^010\\d{8}$", message = "올바르지 않은 휴대폰 번호 형식입니다.")
    String phoneNumber,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate
) {
}
