package atwoz.atwoz.profileimage.application.dto;

import atwoz.atwoz.profileimage.domain.ProfileImage;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ProfileImageUploadResponse(
        String imageUrl,
        Integer order,
        Boolean isPrimary
) {
    static public ProfileImageUploadResponse from(ProfileImage profileImage) {
        return ProfileImageUploadResponse.builder()
                .imageUrl(profileImage.getUrl())
                .order(profileImage.getOrder())
                .isPrimary(profileImage.isPrimary())
                .build();
    }

    static public List<ProfileImageUploadResponse> toResponse(List<ProfileImage> profileImages) {
        return profileImages.stream().map(ProfileImageUploadResponse::from).collect(Collectors.toList());
    }
}
