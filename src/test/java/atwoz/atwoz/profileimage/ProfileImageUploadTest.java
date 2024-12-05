package atwoz.atwoz.profileimage;

import atwoz.atwoz.profileimage.application.ProfileImageService;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ProfileImageUploadTest {

    private ProfileImage profileImage;

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private S3Uploader s3Uploader;

    @BeforeEach
    void setup() {
        profileImage = ProfileImage.of(1L, "url", true);
    }

    @Test
    @DisplayName("이미지 파일이 아닌 경우, 업로드 실패.")
    public void isFailWhenFileIsNotImage() {
        // Given
        MultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        Long memberId = 1L;

        // When & Then
        Assertions.assertThatThrownBy(() -> profileImageService.save(memberId, textFile, true)).isInstanceOf(InvalidImageFileException.class);
    }

    @Test
    @DisplayName("이미지 파일이면서 대표 이미지인 경우, 업로드 성공.")
    public void saveWhenFileIsImageAndIsPrimaryTrue() {
        // Given
        MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
        Long memberId = 1L;

        Mockito.when(profileImageRepository.save(Mockito.any(ProfileImage.class))).thenReturn(profileImage);
        Mockito.when(s3Uploader.uploadFile(Mockito.any(MultipartFile.class))).thenReturn("imageUrl");


        // Then
        ProfileImageUploadResponse profileImageUploadResponse = profileImageService.save(memberId, imageFile, true);
        Assertions.assertThat(profileImageUploadResponse).isNotNull();
        Assertions.assertThat(profileImageUploadResponse.isPrimary()).isTrue();
    }

    @Test
    @DisplayName("이미지 파일이면서 대표 이미지가 아닌 경우, 업로드 성공.")
    public void saveWhenFileIsImageAndIsPrimaryFalse() {
        // Given
        MultipartFile imageFile = new MockMultipartFile("file", "image.jpeg", "image/jpeg", "image".getBytes());
        Long memberId = 1L;

        Mockito.when(profileImageRepository.save(Mockito.any(ProfileImage.class))).thenReturn(profileImage);
        Mockito.when(s3Uploader.uploadFile(Mockito.any(MultipartFile.class))).thenReturn("imageUrl");

        // Then
        ProfileImageUploadResponse profileImageUploadResponse = profileImageService.save(memberId, imageFile, false);
        Assertions.assertThat(profileImageUploadResponse).isNotNull();
        Assertions.assertThat(profileImageUploadResponse.isPrimary()).isFalse();
    }
}
