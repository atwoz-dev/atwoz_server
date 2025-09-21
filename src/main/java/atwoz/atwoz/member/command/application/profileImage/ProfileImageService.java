package atwoz.atwoz.member.command.application.profileImage;

import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.EmptyImageUploadException;
import atwoz.atwoz.member.command.application.profileImage.exception.ProfileImageMemberIdMismatchException;
import atwoz.atwoz.member.command.application.profileImage.exception.ProfileImageNotFoundException;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageCommandRepository profileImageCommandRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requests) {
        // 삭제 되상이 될 프로필 이미지 리스트.
        List<ProfileImageUploadRequest> deleteRequest = requests.stream().filter(
            ProfileImageUploadRequest::getIsDeleted).toList();

        // 삭제 처리.
        delete(deleteRequest);

        // 업로드 대상이 될 프로필 이미지 리스트.
        List<ProfileImageUploadRequest> updatedOrUploadRequests = requests.stream().filter(
            request -> !request.getIsDeleted()
        ).toList();

        // Order 재정렬.
        setOrderFromRequests(updatedOrUploadRequests);

        validateImageUploadRequest(updatedOrUploadRequests);

        List<ProfileImage> profileImages = profileImageCommandRepository.findByMemberId(memberId);

        // 비동기로 s3 요청.
        List<CompletableFuture<ProfileImage>> futures = updatedOrUploadRequests.stream()
            .map(request -> handleImageUpload(request, memberId, profileImages))
            .collect(Collectors.toList());


        // 비동기 결과 병합 (기존 엔티티 + 새롭게 추가된 엔티티).
        List<ProfileImage> profileImageList = gatherProfileImages(futures);

        // DB 반영.
        profileImageCommandRepository.saveAll(profileImageList);
        return ProfileImageMapper.toList(profileImageList);
    }

    private void delete(List<ProfileImageUploadRequest> requests) {
        List<Long> targets = new ArrayList<>();
        // 비동기 삭제 처리.
        for (ProfileImageUploadRequest request : requests) {
            targets.add(request.getId());
            s3Uploader.deleteFile(request.getUrl());
        }

        profileImageCommandRepository.delete(targets);
    }

    private List<ProfileImage> gatherProfileImages(List<CompletableFuture<ProfileImage>> futures) {
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList())
            .join();
    }

    private void validateImageUploadRequest(List<ProfileImageUploadRequest> requests) {
        requests.forEach(r -> {
            MultipartFile image = r.getImage();
            if (image == null && r.getId() == null) {
                throw new EmptyImageUploadException();
            }
        });
    }

    private ProfileImage findByIdAndMemberId(Long profileImageId, Long memberId) {
        ProfileImage profileImage = profileImageCommandRepository.findById(profileImageId)
            .orElseThrow(ProfileImageNotFoundException::new);
        if (!profileImage.getMemberId().equals(memberId)) {
            throw new ProfileImageMemberIdMismatchException();
        }
        return profileImage;
    }

    private ProfileImage findById(Long profileImageId, List<ProfileImage> profileImages) {
        return profileImages.stream().filter(p -> p.getId().equals(profileImageId)).findFirst().orElse(null);
    }

    private CompletableFuture<ProfileImage> handleImageUpload(ProfileImageUploadRequest request, Long memberId,
        List<ProfileImage> profileImages) {
        return s3Uploader.uploadImageAsync(request.getImage())
            .thenApply(imageUrl -> processUploadedImage(request, memberId, profileImages, imageUrl));
    }

    private ProfileImage processUploadedImage(ProfileImageUploadRequest request, Long memberId,
        List<ProfileImage> profileImages, String imageUrl) {
        if (isReplacedImage(request, imageUrl)) { // 기존 프로필 이미지의 파일을 교체하는 경우.
            ProfileImage profileImage = findById(request.getId(), profileImages);
            s3Uploader.deleteFile(profileImage.getUrl()); // 기존 이미지 삭제.
            profileImage.updateUrl(imageUrl);
            profileImage.setOrder(request.getOrder());
            return profileImage;
        } else if (request.getId() != null) { // 기존 프로필 이미지를 유지하는 경우.
            ProfileImage profileImage = findById(request.getId(), profileImages);
            profileImage.setOrder(request.getOrder());
            return findById(request.getId(), profileImages);
        } else { // 새로운 프로필 이미지를 추가하는 경우.
            return createNewProfileImage(request, memberId, imageUrl);
        }
    }

    private boolean isReplacedImage(ProfileImageUploadRequest request, String imageUrl) {
        return request.getId() != null && imageUrl != null;
    }

    private ProfileImage createNewProfileImage(ProfileImageUploadRequest request, Long memberId, String imageUrl) {
        return ProfileImage.builder()
            .memberId(memberId)
            .imageUrl(ImageUrl.from(imageUrl))
            .order(request.getOrder())
            .build();
    }

    private void setOrderFromRequests(List<ProfileImageUploadRequest> requests) {
        for (int i = 0; i < requests.size(); i++) {
            requests.get(i).setOrder(i);
        }
    }
}
