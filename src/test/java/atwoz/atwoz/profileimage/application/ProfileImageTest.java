package atwoz.atwoz.profileimage.application;

import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadRequest;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.application.profileImage.exception.*;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
        @Test
        @DisplayName("이미지 파일이 아닌 경우, 단일 업로드 실패.")
        public void isFailWhenFileIsNotImage() {
            // Given
            MultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
            Long memberId = 1L;

            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(textFile, true, 1));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request)).isInstanceOf(InvalidImageFileException.class);
        }

        @Test
        @DisplayName("대표 이미지가 존재하는 경우, 대표 이미지 단일 업로드 실패.")
        public void isFailWhenUploadPrimaryImageIfAlreadyExists() {
            // Given
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "test".getBytes());
            Long memberId = 1L;

            Mockito.when(profileImageCommandRepository.existsByMemberIdAndIsPrimary(memberId)).thenReturn(true);
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(imageFile, true, 1));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request)).isInstanceOf(PrimaryImageAlreadyExistsException.class);
        }

        @Test
        @DisplayName("이미지 파일이면서 대표 이미지인 경우, 단일 업로드 성공.")
        public void saveWhenFileIsImageAndIsPrimaryTrue() {
            // Given
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            Long memberId = 1L;
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(imageFile, true, 1));

            Mockito.when(s3Uploader.uploadFile(Mockito.any(MultipartFile.class))).thenReturn("imageUrl");

            // When
            List<ProfileImageUploadResponse> profileImageUploadResponse = profileImageService.save(memberId, request);

            // Then
            Assertions.assertThat(profileImageUploadResponse).isNotNull();
            Assertions.assertThat(profileImageUploadResponse.getFirst().isPrimary()).isTrue();
        }

        @Test
        @DisplayName("이미지 파일이면서 대표 이미지가 아닌 경우, 단일 업로드 성공.")
        public void saveWhenFileIsImageAndIsPrimaryFalse() {
            // Given
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            Long memberId = 1L;
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(imageFile, false, 1));

            Mockito.when(s3Uploader.uploadFile(Mockito.any(MultipartFile.class))).thenReturn("imageUrl");

            // When
            List<ProfileImageUploadResponse> profileImageUploadResponse = profileImageService.save(memberId, request);

            // Then
            Assertions.assertThat(profileImageUploadResponse).isNotNull();
            Assertions.assertThat(profileImageUploadResponse.getFirst().isPrimary()).isFalse();
        }

        @Test
        @DisplayName("이미지 파일이 아닌 요청이 포함되어 있는 경우, 다중 업로드 실패")
        public void isFailWhenInvalidFileRequest() {
            // Given
            Long memberId = 1L;
            MultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
            MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(textFile, true, 1), new ProfileImageUploadRequest(imageFile, false, 1));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request)).isInstanceOf(InvalidImageFileException.class);
        }

        @Test
        @DisplayName("대표 이미지가 두 개 포함되어 있는 경우, 다중 업로드 실패")
        public void isFailWhenMultiplePrimaryImages() {
            // Given
            Long memberId = 1L;
            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(imageFile1, true, 1), new ProfileImageUploadRequest(imageFile2, true, 1));

            // When & Then
            Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, request)).isInstanceOf(InvalidPrimaryProfileImageCountException.class);
        }

        @Test
        @DisplayName("대표 이미지 1개가 포함되어 있으며 모든 파일이 이미지 파일인 경우, 성공")
        public void saveMultipleProfileImages() {
            // Given
            Long memberId = 1L;
            MultipartFile imageFile1 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile2 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            MultipartFile imageFile3 = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
            List<ProfileImageUploadRequest> request = List.of(new ProfileImageUploadRequest(imageFile1, true, 1),
                    new ProfileImageUploadRequest(imageFile2, false, 2),
                    new ProfileImageUploadRequest(imageFile3, false, 3));
            // When
            Mockito.when(s3Uploader.uploadFile(Mockito.any(MultipartFile.class))).thenReturn("imageUrl");
            List<ProfileImageUploadResponse> profileImageUploadResponse = profileImageService.save(memberId, request);

            // Then
            Assertions.assertThat(profileImageUploadResponse).isNotNull();
            Assertions.assertThat(profileImageUploadResponse.getFirst().isPrimary()).isTrue();
            for (int i = 1; i <= profileImageUploadResponse.size(); i++) {
                Assertions.assertThat(profileImageUploadResponse.get(i - 1).order()).isEqualTo(i);
            }
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
            ProfileImage profileImage = ProfileImage.builder()
                    .memberId(2L)
                    .imageUrl(ImageUrl.from("url"))
                    .order(1)
                    .isPrimary(true)
                    .build();
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
            ProfileImage profileImage = ProfileImage.builder()
                    .memberId(memberId)
                    .imageUrl(ImageUrl.from("url"))
                    .order(1)
                    .isPrimary(true)
                    .build();
            Mockito.when(profileImageCommandRepository.findById(profileImageId))
                    .thenReturn(Optional.of(profileImage));

            // When & Then
            Assertions.assertThatCode(() -> profileImageService.delete(profileImageId, memberId)).doesNotThrowAnyException();
        }
    }

}
