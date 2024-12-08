package atwoz.atwoz.profileimage.application.dto;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
        MultipartFile image,
        Boolean isPrimary,
        Integer order
) {
}




