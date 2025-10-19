package atwoz.atwoz.member.command.application.profileimage;

import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImageCommandRepository;
import atwoz.atwoz.member.command.infra.profileImage.S3Uploader;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private ProfileImageCommandRepository profileImageCommandRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Test
    @DisplayName("pre-signed URL 테스트")
    void getPreSignedUrl() {
        // Given
        String fileName = "TEMP_IMAGE.jpeg";

        // When
        profileImageService.getPresignedUrl(fileName);

        // Then
        verify(s3Uploader.getPreSignedUrl(fileName));
    }

    @Test
    @DisplayName("이미지 업로드 테스트")
    void uploadImage() {
        // Given
        List<ProfileImageUploadRequest> request = List.of(
            new ProfileImageUploadRequest("TEMP_IMAGE_1.jpeg"),
            new ProfileImageUploadRequest("TEMP_IMAGE_2.jpeg"),
            new ProfileImageUploadRequest("TEMP_IMAGE_3.jpeg"),
            new ProfileImageUploadRequest("TEMP_IMAGE_4.jpeg")
        );
    }

}
