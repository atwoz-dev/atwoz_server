package atwoz.atwoz.profileimage.application.dto;

import lombok.Builder;

@Builder
public record ProfileImageUploadResponse(
        String imageUrl,
        Integer order,
        Boolean isPrimary
) {
}
