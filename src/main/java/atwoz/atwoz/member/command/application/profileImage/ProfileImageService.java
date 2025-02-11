package atwoz.atwoz.member.command.application.profileImage;

import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadRequest;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.*;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageCommandRepository profileImageCommandRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requestList) {
        validateRequestList(memberId, requestList);

        List<CompletableFuture<ProfileImage>> future = requestList.stream()
                .map(request -> s3Uploader.uploadImageAsync(request.getImage())
                        .thenApply(imageUrl -> ProfileImage.builder()
                                .memberId(memberId)
                                .imageUrl(ImageUrl.from(imageUrl))
                                .order(request.getOrder())
                                .isPrimary(request.getIsPrimary())
                                .build())
                ).toList();

        List<ProfileImage> profileImageList = gatherProfileImages(future);

        profileImageCommandRepository.saveAll(profileImageList);
        return ProfileImageMapper.toList(profileImageList);
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        ProfileImage profileImage = findByIdAndMemberId(id, memberId);
        s3Uploader.deleteFile(profileImage.getUrl());
        profileImageCommandRepository.delete(profileImage);
    }

    private List<ProfileImage> gatherProfileImages(List<CompletableFuture<ProfileImage>> futures) {
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();
    }

    private void validateRequestList(Long memberId, List<ProfileImageUploadRequest> requestList) {
        long primaryCount = requestList.stream().filter(ProfileImageUploadRequest::getIsPrimary)
                .count();

        if (primaryCount > 1) {
            throw new InvalidPrimaryProfileImageCountException();
        }

        for (ProfileImageUploadRequest request : requestList) {
            validateImageType(request.getImage());
            checkPrimaryImageAlreadyExists(memberId, request.getIsPrimary());
        }
    }

    private void validateImageType(MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidImageFileException();
        }
    }

    private void checkPrimaryImageAlreadyExists(Long memberId, Boolean isPrimary) {
        if (isPrimary && profileImageCommandRepository.existsByMemberIdAndIsPrimary(memberId)) {
            throw new PrimaryImageAlreadyExistsException();
        }
    }

    private ProfileImage findByIdAndMemberId(Long profileImageId, Long memberId) {
        ProfileImage profileImage = profileImageCommandRepository.findById(profileImageId).orElseThrow(() -> new ProfileImageNotFoundException());
        if (profileImage.getMemberId() != memberId) {
            throw new ProfileImageMemberIdMismatchException();
        }
        return profileImage;
    }
}
