package atwoz.atwoz.member.presentation.profileimage.dto;

import jakarta.validation.constraints.NotEmpty;

public record PresignedUrlPostRequest(
    @NotEmpty(message = "업로드할 파일 이름을 입력해주세요.")
    String fileName
) {
}
