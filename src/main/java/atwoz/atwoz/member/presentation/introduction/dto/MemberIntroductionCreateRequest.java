package atwoz.atwoz.member.presentation.introduction.dto;

import jakarta.validation.constraints.NotNull;

public record MemberIntroductionCreateRequest(
    @NotNull(message = "introducedMemberId는 필수입니다.")
    Long introducedMemberId
) {
}
