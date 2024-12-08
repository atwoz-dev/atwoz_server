package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.exception.PrimaryImageAlreadyExistsException;
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

        List<ProfileImage> resultFuture = CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new))
                .thenApply(v -> futureList.stream()
                        .map(CompletableFuture::join)
                        .toList()).join();

        profileImageRepository.saveAll(resultFuture);

        return ProfileImageUploadResponse.from(resultFuture);
    }

    @Async
    protected CompletableFuture<ProfileImage> uploadImageAsync(Long memberId, ProfileImageUploadRequest request) {
        checkPrimaryImageAlreadyExists(memberId, request.isPrimary());
        validateImageType(request.image());
        String imageUrl = s3Uploader.uploadFile(request.image());

        ProfileImage profileImage = ProfileImage.of(memberId, imageUrl, request.order(), request.isPrimary());

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


}
