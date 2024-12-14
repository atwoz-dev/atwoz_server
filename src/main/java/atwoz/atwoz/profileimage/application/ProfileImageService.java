package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.exception.PrimaryImageAlreadyExistsException;
import atwoz.atwoz.profileimage.exception.ProfileImageNotFoundException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requestList) {
        List<CompletableFuture<ProfileImage>> futureList = new ArrayList<>();
        for (ProfileImageUploadRequest request : requestList) {
            futureList.add(uploadImageAsync(memberId, request));
        }

        List<ProfileImage> profileImageList = CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new))
                .thenApply(v -> futureList.stream()
                        .map(CompletableFuture::join)
                        .toList()).join();

        profileImageRepository.saveAll(profileImageList);
        return ProfileImageUploadResponse.from(profileImageList);
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        ProfileImage profileImage = profileImageRepository.findByIdAndMemberId(id, memberId).orElseThrow();
        // S3에서 제거.
        s3Uploader.deleteFile(profileImage.getUrl());
        // 레포에서 제거.
        profileImageRepository.delete(profileImage);
    }

    @Async
    protected CompletableFuture<ProfileImage> uploadImageAsync(Long memberId, ProfileImageUploadRequest request) {
        checkPrimaryImageAlreadyExists(memberId, request.getIsPrimary());
        validateImageType(request.getImage());
        String imageUrl = s3Uploader.uploadFile(request.getImage());

        ProfileImage profileImage = ProfileImage.of(memberId, imageUrl, request.getOrder(), request.getIsPrimary());
        return CompletableFuture.completedFuture(profileImage);
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

    private ProfileImage findByIdAndMemberId(Long memberId, Long profileImageId) {
        return profileImageRepository.findByIdAndMemberId(profileImageId, memberId).orElseThrow(() -> new ProfileImageNotFoundException());
    }
}
