package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.application.AdminAuthService;
import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AdminSignupResponse>> signUp(@Valid @RequestBody AdminSignupRequest request) {
        AdminSignupResponse data = adminAuthService.signUp(request);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, data));
    }
}
