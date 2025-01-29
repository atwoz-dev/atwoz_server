package atwoz.atwoz.member.command.application.profileImage.dto;

import lombok.Builder;

@Builder
public record ProfileImageUploadResponse(
        String imageUrl,
        Integer order,
        Boolean isPrimary
) {
}
