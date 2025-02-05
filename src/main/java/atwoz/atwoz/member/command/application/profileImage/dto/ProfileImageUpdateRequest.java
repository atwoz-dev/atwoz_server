package atwoz.atwoz.member.command.application.profileImage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUpdateRequest {
    Long id;

    MultipartFile image;

    @NotNull(message = "대표 프로필 여부를 입력해주세요.")
    Boolean isPrimary;

    @NotNull(message = "해당 이미지의 순서를 입력해주세요.")
    Integer order;
}
