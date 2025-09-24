package atwoz.atwoz.member.presentation.profileimage.dto;

import atwoz.atwoz.member.presentation.profileimage.dto.support.ValidImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUploadRequest {
    Long id;

    @ValidImage
    MultipartFile image;

    String url;

    Boolean isDeleted;

    Integer order;
}