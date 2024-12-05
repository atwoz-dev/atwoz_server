package atwoz.atwoz.profileimage.application.dto;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import lombok.Builder;

@Builder
public record ProfileImageUploadResponse(
        String imageUrl,
        Boolean isPrimary
) {
    static public ProfileImageUploadResponse from(ProfileImage profileImage) {
        return ProfileImageUploadResponse.builder()
                .imageUrl(profileImage.getUrl())
                .isPrimary(profileImage.isPrimary())
                .build();
    }
}
