package atwoz.atwoz.member.presentation.profileimage.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUploadRequest {
    @NotEmpty(message = "이미지 URL을 입력해주세요")
    String imageUrl;
}