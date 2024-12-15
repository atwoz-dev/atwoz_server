package atwoz.atwoz.profileimage.presentation;

import atwoz.atwoz.common.auth.presentation.support.AuthMember;
import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import atwoz.atwoz.profileimage.application.ProfileImageService;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadRequestWrapper;
import atwoz.atwoz.profileimage.application.dto.ProfileImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<ProfileImageUploadResponse>>> uploadProfileImage(@ModelAttribute ProfileImageUploadRequestWrapper request, @AuthMember Long memberId) {
        BaseResponse<List<ProfileImageUploadResponse>> response = new BaseResponse(StatusType.OK, profileImageService.save(memberId, request.getRequests()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
