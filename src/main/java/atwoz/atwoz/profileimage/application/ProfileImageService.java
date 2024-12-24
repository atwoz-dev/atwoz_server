package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.exception.*;
import atwoz.atwoz.profileimage.infra.S3Uploader;
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

    private final ProfileImageRepository profileImageRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requestList) {
        validateRequestList(memberId, requestList);

        List<CompletableFuture<ProfileImage>> future = requestList.stream()
                .map(request -> uploadImageAsync(request.getImage())
                        .thenApply(imageUrl -> ProfileImage.builder()
                                .memberId(memberId)
                                .imageUrl(ImageUrl.from(imageUrl))
                                .order(request.getOrder())
                                .isPrimary(request.getIsPrimary())
                                .build())
                ).toList();

        List<ProfileImage> profileImageList = gatherProfileImages(future);

        profileImageRepository.saveAll(profileImageList);
        return ProfileImageUploadResponse.toResponse(profileImageList);
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        ProfileImage profileImage = findByIdAndMemberId(id, memberId);
        s3Uploader.deleteFile(profileImage.getUrl());
        profileImageRepository.delete(profileImage);
    }

    @Async
    protected CompletableFuture<String> uploadImageAsync(MultipartFile image) {
        String imageUrl = s3Uploader.uploadFile(image);
        return CompletableFuture.completedFuture(imageUrl);
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
        if (isPrimary && profileImageRepository.existsPrimaryImageByMemberId(memberId)) {
            throw new PrimaryImageAlreadyExistsException();
        }
    }

    private ProfileImage findByIdAndMemberId(Long profileImageId, Long memberId) {
        ProfileImage profileImage = profileImageRepository.findById(profileImageId).orElseThrow(() -> new ProfileImageNotFoundException());
        if (profileImage.getMemberId() != memberId) {
            throw new ProfileImageMemberIdMismatchException();
        }
        return profileImage;
    }
}
