package atwoz.atwoz.member.presentation.member.dto;

import jakarta.validation.constraints.Pattern;

public record MemberChangeToActiveRequest(
    @Pattern(regexp = "^010\\d{8}$", message = "010으로 시작하고 8자리의 숫자가 와야합니다.")
    String phoneNumber
) {
}
