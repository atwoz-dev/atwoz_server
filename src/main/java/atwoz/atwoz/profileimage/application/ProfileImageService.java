package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.exception.InvalidPrimaryProfileImageCountException;
import atwoz.atwoz.profileimage.exception.PrimaryImageAlreadyExistsException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
                        .thenApply(imageUrl -> ProfileImage.of(memberId, imageUrl, request.getOrder(), request.getIsPrimary()))).toList();

        List<ProfileImage> profileImageList = CompletableFuture.allOf(future.toArray(CompletableFuture[]::new))
                .thenApply(v -> future.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();

        profileImageRepository.saveAll(profileImageList);
        return ProfileImageUploadResponse.from(profileImageList);
    }

    @Async
    protected CompletableFuture<String> uploadImageAsync(MultipartFile image) {
        String imageUrl = s3Uploader.uploadFile(image);
        return CompletableFuture.completedFuture(imageUrl);
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
        if (isPrimary && profileImageRepository.existsByMemberIdAndIsPrimary(memberId)) {
            throw new PrimaryImageAlreadyExistsException();
        }
    }
}
