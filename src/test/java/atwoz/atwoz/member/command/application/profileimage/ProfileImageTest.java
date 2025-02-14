package atwoz.atwoz.member.command.application.profileimage;

import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadRequest;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.DuplicateProfileImageOrderException;
import atwoz.atwoz.member.command.application.profileImage.exception.InvalidPrimaryProfileImageCountException;
import atwoz.atwoz.member.command.application.profileImage.exception.ProfileImageMemberIdMismatchException;
import atwoz.atwoz.member.command.application.profileImage.exception.ProfileImageNotFoundException;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class ProfileImageTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private ProfileImageCommandRepository profileImageCommandRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Nested
    class UploadTest {
        List<ProfileImageUploadRequest> goodRequests;
        List<ProfileImage> existsProfileImages;

        @BeforeEach
        void setUp() {
            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            goodRequests = List.of(new ProfileImageUploadRequest(1L, imageFile1, false, 3), new ProfileImageUploadRequest(null, imageFile2, false, 4));

            ProfileImage profileImage1 = ProfileImage.builder()
                    .memberId(1L)
                    .order(1)
                    .imageUrl(ImageUrl.from("imageUrl"))
                    .isPrimary(true)
                    .build();

            ProfileImage profileImage2 = ProfileImage.builder()
                    .memberId(1L)
                    .order(2)
                    .imageUrl(ImageUrl.from("imageUrl"))
                    .isPrimary(false)
                    .build();

            ReflectionTestUtils.setField(profileImage1, "id", 1L);
            ReflectionTestUtils.setField(profileImage2, "id", 2L);

            existsProfileImages = List.of(profileImage1, profileImage2);
        }

        @Test
        @DisplayName("대표 이미지가 이미 존재하는 경우. 새롭게 대표 이미지를 업로드하면 실패.")
        public void isFailWhenUploadPrimaryImageIfAlreadyExists() {
            // Given
            Long memberId = 1L;
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(null, imageFile, true, 3));

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(existsProfileImages);

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request))
                    .isInstanceOf(InvalidPrimaryProfileImageCountException.class);
        }

        @Test
        @DisplayName("순서가 중복되는 경우 실패.")
        public void isFailWhenOrderIsDuplicated() {
            // Given
            Long memberId = 1L;
            int duplicatedOrder = 3;
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(null, imageFile, true, duplicatedOrder));


            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(
                    List.of(ProfileImage.builder()
                            .isPrimary(true)
                            .imageUrl(ImageUrl.from("imageUrl"))
                            .memberId(1L)
                            .order(duplicatedOrder)
                            .build())
            );

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request))
                    .isInstanceOf(DuplicateProfileImageOrderException.class);
        }

        @Test
        @DisplayName("존재하지 않는 프로필 이미지를 업데이트하는 경우, 업데이트 실패")
        public void isFailWhenUpdateNotExistedProfileImage() {
            // Given
            Long memberId = 1L;
            Long notExistedId = 2L;
            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> requests = List.of(new ProfileImageUploadRequest(notExistedId, imageFile1, true, 3), new ProfileImageUploadRequest(null, imageFile2, false, 4));

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(List.of());

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, requests))
                    .isInstanceOf(ProfileImageNotFoundException.class);
        }

        @Test
        @DisplayName("정상적인 요청의 경우, 초기 업로드 성공.")
        public void saveWhenProfileImageNotExists() {
            // Given
            Long memberId = 1L;

            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> firstRequests = List.of(new ProfileImageUploadRequest(null, imageFile1, true, 3), new ProfileImageUploadRequest(null, imageFile2, false, 4));

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(List.of());
            Mockito.when(s3Uploader.uploadImageAsync(Mockito.any())).thenReturn(CompletableFuture.completedFuture("imageUrl"));

            // When
            List<ProfileImageUploadResponse> response = profileImageService.save(memberId, firstRequests);

            // Then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response).size().isEqualTo(firstRequests.size());

            for (ProfileImageUploadResponse profileImageUploadResponse : response) {
                Assertions.assertThat(profileImageUploadResponse).isNotNull();
                Assertions.assertThat(profileImageUploadResponse.imageUrl()).isEqualTo("imageUrl");
            }
        }

        @Test
        @DisplayName("기존 프로필 이미지 업데이트와 업로드 함께 성공.")
        public void uploadWithUpdate() {
            // Given
            Long memberId = 1L;

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(existsProfileImages);
            Mockito.when(s3Uploader.uploadImageAsync(Mockito.any())).thenReturn(CompletableFuture.completedFuture("imageUrl"));

            // When
            List<ProfileImageUploadResponse> response = profileImageService.save(memberId, goodRequests);

            // Then
            Assertions.assertThat(response).isNotNull();
            for (ProfileImageUploadResponse profileImageUploadResponse : response) {
                Assertions.assertThat(profileImageUploadResponse).isNotNull();
                Assertions.assertThat(profileImageUploadResponse.imageUrl()).isEqualTo("imageUrl");
            }

            ProfileImage profileImage = existsProfileImages.stream().filter(i -> i.getId() == 1L)
                    .findFirst().orElseThrow(RuntimeException::new);
            ProfileImageUploadRequest request = goodRequests.stream().filter(i -> i.getId() == 1L)
                    .findFirst().orElseThrow(RuntimeException::new);

            Assertions.assertThat(profileImage.getId()).isEqualTo(request.getId());
            Assertions.assertThat(profileImage.getOrder()).isEqualTo(request.getOrder());
            Assertions.assertThat(profileImage.isPrimary()).isEqualTo(request.getIsPrimary());
        }
    }

    @Nested
    class DeleteTest {
        @Test
        @DisplayName("프로필 이미지가 존재하지 않는 경우, 이미지 삭제 실패")
        public void isFailWhenProfileImageNotExists() {
            // Given
            Long profileImageId = 1L;
            Long memberId = 1L;
            Mockito.when(profileImageCommandRepository.findById(profileImageId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.delete(profileImageId, memberId)).isInstanceOf(ProfileImageNotFoundException.class);
        }

        @Test
        @DisplayName("프로필 이미지가 존재하지만 해당 유저의 이미지가 아닌 경우, 이미지 삭제 실패")
        public void isFailWhenMemberIdNotEquals() {
            // Given
            Long profileImageId = 1L;
            Long memberId = 1L;
            ProfileImage profileImage = ProfileImage.builder().memberId(2L).imageUrl(ImageUrl.from("url")).order(1).isPrimary(true).build();
            Mockito.when(profileImageCommandRepository.findById(profileImageId)).thenReturn(Optional.of(profileImage));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.delete(profileImageId, memberId)).isInstanceOf(ProfileImageMemberIdMismatchException.class);
        }

        @Test
        @DisplayName("프로필 이미지가 존재하는 경우, 이미지 삭제 성공")
        public void deleteWhenProfileImageExists() {
            // Given
            Long profileImageId = 1L;
            Long memberId = 1L;
            ProfileImage profileImage = ProfileImage.builder().memberId(memberId).imageUrl(ImageUrl.from("url")).order(1).isPrimary(true).build();
            Mockito.when(profileImageCommandRepository.findById(profileImageId)).thenReturn(Optional.of(profileImage));

            // When & Then
            Assertions.assertThatCode(() -> profileImageService.delete(profileImageId, memberId)).doesNotThrowAnyException();
        }
    }

}
