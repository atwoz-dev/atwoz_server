package atwoz.atwoz.member.command.application.profileImage;

import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadRequest;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.*;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageCommandRepository profileImageCommandRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<ProfileImageUploadResponse> save(Long memberId, List<ProfileImageUploadRequest> requestList) {
        // 빈 파일로 이미지를 업데이트하려는 경우 검증.
        validateImageUploadRequest(requestList);

        List<ProfileImage> profileImages = profileImageCommandRepository.findByMemberId(memberId);

        // 새롭게 추가되는 프로필 이미지.
        List<ProfileImageUploadRequest> imageUploadRequests = requestList.stream().filter(r -> r.getId() == null).toList();

        // 기존 이미지를 업데이트.
        List<ProfileImageUploadRequest> imageUpdateRequests = requestList.stream().filter(r -> r.getId() != null).toList();

        // 업데이트 요청을 엔티티에 반영.
        updateByRequests(imageUpdateRequests, profileImages);


        // 반영된 엔티티와 업로드 요청을 합쳐서, 검증.
        validateRequestsWithProfileImages(imageUploadRequests, profileImages);

        // 비동기로 s3 요청.
        List<CompletableFuture<ProfileImage>> futures = imageUploadRequests.stream()
                .map(request -> handleImageUpload(request, memberId, profileImages))
                .collect(Collectors.toList());


        // 비동기 결과 병합 (기존 엔티티 + 새롭게 추가된 엔티티).
        List<ProfileImage> profileImageList = gatherProfileImages(futures);

        // DB 반영.
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

    private void updateByRequests(List<ProfileImageUploadRequest> imageUpdateRequests, List<ProfileImage> profileImages) {
        imageUpdateRequests.forEach(r -> {
            ProfileImage profileImage = profileImages.stream().filter(p -> p.getId().equals(r.getId()))
                    .findFirst()
                    .orElseThrow(ProfileImageNotFoundException::new);

            profileImage.updateBasicInfo(r.getOrder(), r.getIsPrimary());
        });
    }

    private void validateRequestsWithProfileImages(List<ProfileImageUploadRequest> imageUploadRequests, List<ProfileImage> profileImages) {
        validateOrderUniqueness(imageUploadRequests, profileImages);
        validatePrimaryImageCount(imageUploadRequests, profileImages);
    }

    private void validateOrderUniqueness(List<ProfileImageUploadRequest> imageUploadRequests, List<ProfileImage> profileImages) {
        Set<Integer> orderSet = new HashSet<>();

        // 중복된 order 확인: 요청 리스트와 프로필 이미지 리스트 합쳐서 처리
        Stream.concat(imageUploadRequests.stream(), profileImages.stream())
                .map(item -> item instanceof ProfileImageUploadRequest ? ((ProfileImageUploadRequest) item).getOrder() : ((ProfileImage) item).getOrder())
                .forEach(order -> {
                    if (!orderSet.add(order)) {
                        throw new DuplicateProfileImageOrderException();
                    }
                });
    }

    private void validatePrimaryImageCount(List<ProfileImageUploadRequest> imageUploadRequests, List<ProfileImage> profileImages) {
        long primaryCount = imageUploadRequests.stream().filter(ProfileImageUploadRequest::getIsPrimary).count() +
                profileImages.stream().filter(ProfileImage::isPrimary).count();

        if (primaryCount > 1) {
            throw new InvalidPrimaryProfileImageCountException();
        }
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
        ProfileImage profileImage = profileImageCommandRepository.findById(profileImageId).orElseThrow(ProfileImageNotFoundException::new);
        if (!profileImage.getMemberId().equals(memberId)) {
            throw new ProfileImageMemberIdMismatchException();
        }
        return profileImage;
    }

    private ProfileImage findById(Long profileImageId, List<ProfileImage> profileImages) {
        return profileImages.stream().filter(p -> p.getId().equals(profileImageId)).findFirst().orElse(null);
    }

    private CompletableFuture<ProfileImage> handleImageUpload(ProfileImageUploadRequest request, Long memberId, List<ProfileImage> profileImages) {
        return s3Uploader.uploadImageAsync(request.getImage())
                .thenApply(imageUrl -> processUploadedImage(request, memberId, profileImages, imageUrl));
    }

    private ProfileImage processUploadedImage(ProfileImageUploadRequest request, Long memberId, List<ProfileImage> profileImages, String imageUrl) {
        if (request.getId() != null && imageUrl != null) { // 기존 프로필 이미지의 파일을 교체하는 경우.
            ProfileImage profileImage = findById(request.getId(), profileImages);
            s3Uploader.deleteFile(profileImage.getUrl()); // 기존 이미지 삭제.
            profileImage.updateUrl(imageUrl);
            return profileImage;
        } else if (request.getId() != null) { // 기존 프로필 이미지를 유지하는 경우.
            return findById(request.getId(), profileImages);
        } else { // 새로운 프로필 이미지를 추가하는 경우.
            return createNewProfileImage(request, memberId, imageUrl);
        }
    }

    private ProfileImage createNewProfileImage(ProfileImageUploadRequest request, Long memberId, String imageUrl) {
        return ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from(imageUrl))
                .order(request.getOrder())
                .isPrimary(request.getIsPrimary())
                .build();
    }
}
