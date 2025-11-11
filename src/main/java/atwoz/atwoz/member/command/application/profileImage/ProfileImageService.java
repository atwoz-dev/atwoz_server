package atwoz.atwoz.member.command.application.profileImage;

import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.ExceedProfileImageCountException;
import atwoz.atwoz.member.command.application.profileImage.exception.InvalidProfileImageExtensionException;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import atwoz.atwoz.member.command.infra.profileImage.dto.PresignedUrlResponse;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(
        "jpg", "jpeg", "png", "webp", "heic"
    );
    private final ProfileImageCommandRepository profileImageCommandRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requests) {
        /**
         * Request Validate.
         */
        if (requests == null) {
            requests = new ArrayList<>();
        }

        validateRequestSize(requests);

        /**
         * Delete Existed Entity.
         */
        profileImageCommandRepository.deleteByMemberId(memberId);

        /**
         * Save ProfileImage Entity
         */
        List<ProfileImage> profileImages = new ArrayList<>();
        int order = 1;

        for (ProfileImageUploadRequest request : requests) {
            ProfileImage profileImage = ProfileImage
                .builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from(request.imageUrl()))
                .order(order)
                .isPrimary(order == 1)
                .build();

            profileImages.add(profileImage);
            order++;
        }

        profileImageCommandRepository.saveAll(profileImages);
        return ProfileImageMapper.toList(profileImages);
    }

    public PresignedUrlResponse getPresignedUrl(String fileName, Long userId) {
        validateFileName(fileName);
        return s3Uploader.getPreSignedUrl(fileName, userId);
    }

    private void validateRequestSize(List<ProfileImageUploadRequest> request) {
        /**
         * 사이즈 검증.
         */
        if (request.size() > 6) {
            throw new ExceedProfileImageCountException(request.size());
        }
    }

    private void validateFileName(String fileName) {
        /**
         * 확장자 검증.
         */
        if (fileName == null || fileName.isEmpty()) {
            throw new InvalidProfileImageExtensionException("Blank FileName.");
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new InvalidProfileImageExtensionException("No Extension.");
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new InvalidProfileImageExtensionException(extension);
        }
    }
}
