package atwoz.atwoz.member.command.application.profileImage;

import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageCommandRepository profileImageCommandRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requests) {
        /**
         * Request Validate.
         */
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
                .imageUrl(ImageUrl.from(request.getImageUrl()))
                .order(order++)
                .isPrimary(order == 1)
                .build();
        }

        profileImageCommandRepository.saveAll(profileImages);
        return ProfileImageMapper.toList(profileImages);
    }

    public String getPresignedUrl(String fileName) {
        validateFileName(fileName);
        return s3Uploader.getPreSignedUrl(fileName);
    }

    private void validateRequestSize(List<ProfileImageUploadRequest> request) {
        /**
         * 사이즈 검증.
         */

    }

    private void validateFileName(String fileName) {
        /**
         * 확장자 검증.
         */
    }
}
