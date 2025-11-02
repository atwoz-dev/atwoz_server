package atwoz.atwoz.member.presentation.profileimage;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadResponse;
import atwoz.atwoz.member.command.infra.profileImage.dto.PresignedUrlResponse;
import atwoz.atwoz.member.presentation.profileimage.dto.PresignedUrlPostRequest;
import atwoz.atwoz.member.presentation.profileimage.dto.ProfileImageUploadRequestWrapper;
import atwoz.atwoz.member.query.profileimage.ProfileImageQueryRepository;
import atwoz.atwoz.member.query.profileimage.view.ProfileImageView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로필 이미지 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profileimage")
public class ProfileImageController {

    private final ProfileImageService profileImageService;
    private final ProfileImageQueryRepository profileImageQueryRepository;

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping
    public ResponseEntity<BaseResponse<List<ProfileImageUploadResponse>>> updateProfileImage(
        @ModelAttribute @Valid ProfileImageUploadRequestWrapper request, @AuthPrincipal AuthContext authContext) {
        return ResponseEntity.ok(
            BaseResponse.of(StatusType.OK, profileImageService.save(authContext.getId(), request.getRequests())));
    }

    @Operation(summary = "프로필 이미지 업로드용 preSignedUrl 생성")
    @PostMapping("/presigned-url")
    public ResponseEntity<BaseResponse<PresignedUrlResponse>> getPresignedUrl(
        @RequestBody @Valid PresignedUrlPostRequest request,
        @AuthPrincipal AuthContext authContext) {
        return ResponseEntity.ok(
            BaseResponse.of(StatusType.OK, profileImageService.getPresignedUrl(request.fileName(), authContext.getId()))
        );
    }

    @Operation(summary = "내 프로필 이미지 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<List<ProfileImageView>>> getMyProfileImages(
        @AuthPrincipal AuthContext authContext) {
        List<ProfileImageView> profileImageViews = profileImageQueryRepository.findByMemberId(authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, profileImageViews));
    }
}
