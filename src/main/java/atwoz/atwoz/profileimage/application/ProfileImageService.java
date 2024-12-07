package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.exception.PrimaryImageAlreadyExistsException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final S3Uploader s3Uploader;


    public ProfileImageUploadResponse save(Long memberId, MultipartFile file, Boolean isPrimary) {

        checkPrimaryImageAlreadyExists(memberId, isPrimary);
        validateImageType(file);
        String imageUrl = s3Uploader.uploadFile(file);

        ProfileImage profileImage = ProfileImage.of(memberId, imageUrl, isPrimary);
        profileImageRepository.save(profileImage);

        return ProfileImageUploadResponse.from(profileImage);
    }

    private void validateImageType(MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidImageFileException();
        }
    }

    private void checkPrimaryImageAlreadyExists(Long memberId, Boolean isPrimary) {
        if (isPrimary && profileImageRepository.existsByMemberIdAndIsPrimary(memberId)) {
            throw new PrimaryImageAlreadyExistsException();
        }
    }
}
