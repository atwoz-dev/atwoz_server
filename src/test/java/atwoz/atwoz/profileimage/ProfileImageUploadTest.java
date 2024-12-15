package atwoz.atwoz.profileimage;

import atwoz.atwoz.profileimage.application.ProfileImageService;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequest;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.exception.InvalidImageFileException;
import atwoz.atwoz.profileimage.exception.InvalidPrimaryProfileImageCountException;
import atwoz.atwoz.profileimage.exception.PrimaryImageAlreadyExistsException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProfileImageUploadTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private S3Uploader s3Uploader;

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

        Mockito.when(profileImageRepository.existsPrimaryImageByMemberId(memberId)).thenReturn(true);
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
