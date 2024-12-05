package atwoz.atwoz.profileimage.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
        MultipartFile image,
        Boolean isPrimary
) {
}




