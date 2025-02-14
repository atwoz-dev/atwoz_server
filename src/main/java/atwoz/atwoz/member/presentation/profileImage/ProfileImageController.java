package atwoz.atwoz.member.presentation.profileImage;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.profileImage.ProfileImageService;
import atwoz.atwoz.member.command.application.profileImage.dto.ProfileImageUploadRequestWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profileimage")
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> updateProfileImage(@ModelAttribute @Valid ProfileImageUploadRequestWrapper request, @AuthPrincipal AuthContext authContext) {
        profileImageService.save(authContext.getId(), request.getRequests());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteProfileImage(@PathVariable Long id, @AuthPrincipal AuthContext authContext) {
        profileImageService.delete(id, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

}
