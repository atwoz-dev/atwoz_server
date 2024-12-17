package atwoz.atwoz.profileimage;


import atwoz.atwoz.profileimage.application.ProfileImageService;
import atwoz.atwoz.profileimage.domain.ProfileImage;
import atwoz.atwoz.profileimage.domain.ProfileImageRepository;
import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import atwoz.atwoz.profileimage.exception.ProfileImageMemberIdMismatchException;
import atwoz.atwoz.profileimage.exception.ProfileImageNotFoundException;
import atwoz.atwoz.profileimage.infra.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProfileImageDeleteTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Test
    @DisplayName("프로필 이미지가 존재하지 않는 경우, 이미지 삭제 실패")
    public void isFailWhenProfileImageNotExists() {
        // Given
        Long profileImageId = 1L;
        Long memberId = 1L;
        Mockito.when(profileImageRepository.findById(profileImageId)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> profileImageService.delete(profileImageId, memberId)).isInstanceOf(ProfileImageNotFoundException.class);
    }

    @Test
    @DisplayName("프로필 이미지가 존재하지만 해당 유저의 이미지가 아닌 경우, 이미지 삭제 실패")
    public void isFailWhenMemberIdNotEquals() {
        // Given
        Long profileImageId = 1L;
        Long memberId = 1L;
        Mockito.when(profileImageRepository.findById(profileImageId)).thenReturn(Optional.of(ProfileImage.of(MemberId.from(2L), ImageUrl.from("url"), 1, true)));

        // When & Then
        Assertions.assertThatThrownBy(() -> profileImageService.delete(profileImageId, memberId)).isInstanceOf(ProfileImageMemberIdMismatchException.class);
    }

    @Test
    @DisplayName("프로필 이미지가 존재하는 경우, 이미지 삭제 성공")
    public void deleteWhenProfileImageExists() {
        // Given
        Long profileImageId = 1L;
        Long memberId = 1L;
        Mockito.when(profileImageRepository.findById(profileImageId))
                .thenReturn(Optional.of(ProfileImage.of(MemberId.from(memberId), ImageUrl.from("url"), 1, true)));

        // When & Then
        Assertions.assertThatCode(() -> profileImageService.delete(profileImageId, memberId)).doesNotThrowAnyException();
    }
}
