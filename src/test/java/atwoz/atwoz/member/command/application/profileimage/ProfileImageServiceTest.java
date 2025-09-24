package atwoz.atwoz.member.command.application.profileimage;

import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.EmptyImageUploadException;
import atwoz.atwoz.member.command.application.profileImage.exception.ProfileImageNotFoundException;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequest;
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
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class ProfileImageServiceTest {

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
            goodRequests = List.of(new ProfileImageUploadRequest(1L, imageFile1, null, false, 3),
                new ProfileImageUploadRequest(null, imageFile2, null, false, 4));

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
        @DisplayName("존재하지 않는 프로필 이미지를 업데이트하는 경우, 업데이트 실패")
        public void isFailWhenUpdateNotExistedProfileImage() {
            // Given
            Long memberId = 1L;
            Long notExistedId = 2L;
            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> requests = List.of(
                new ProfileImageUploadRequest(notExistedId, imageFile1, null, false, null),
                new ProfileImageUploadRequest(null, imageFile2, null, false, null));

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(List.of());
            Mockito.when(s3Uploader.uploadImageAsync(Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture("imageUrl"));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, requests))
                .isInstanceOf(ProfileImageNotFoundException.class);
        }

        @Test
        @DisplayName("비어있는 파일을 업로드 대상으로 하는 경우, 업데이트 실패")
        public void isFailWhenUpdateWithEmptyFile() {
            // Given
            Long memberId = 1L;
            List<ProfileImageUploadRequest> emptyFileRequests = List.of(
                new ProfileImageUploadRequest(null, null, null, false, null));


            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, emptyFileRequests))
                .isInstanceOf(EmptyImageUploadException.class);
        }

        @Test
        @DisplayName("정상적인 요청의 경우, 초기 업로드 성공.")
        public void saveWhenProfileImageNotExists() {
            // Given
            Long memberId = 1L;

            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> firstRequests = List.of(
                new ProfileImageUploadRequest(null, imageFile1, null, false, null),
                new ProfileImageUploadRequest(null, imageFile2, null, false, null));

            Mockito.when(profileImageCommandRepository.findByMemberId(memberId)).thenReturn(List.of());
            Mockito.when(s3Uploader.uploadImageAsync(Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture("imageUrl"));

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
            Mockito.when(s3Uploader.uploadImageAsync(Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture("imageUrl"));

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
        }
    }
}
