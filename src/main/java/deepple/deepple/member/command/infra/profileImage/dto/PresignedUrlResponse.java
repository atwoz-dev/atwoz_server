package deepple.deepple.member.command.infra.profileImage.dto;

public record PresignedUrlResponse(
    String presignedUrl,
    String objectUrl
) {
}
