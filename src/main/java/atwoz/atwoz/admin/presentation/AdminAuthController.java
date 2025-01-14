package atwoz.atwoz.admin.presentation;

import atwoz.atwoz.admin.application.AdminAuthService;
import atwoz.atwoz.admin.application.dto.AdminLoginRequest;
import atwoz.atwoz.admin.application.dto.AdminLoginResponse;
import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.auth.presentation.RefreshTokenCookieProperties;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;
    private final AdminAuthService adminAuthService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AdminSignupResponse>> signup(@Valid @RequestBody AdminSignupRequest request) {
        AdminSignupResponse data = adminAuthService.signup(request);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, data));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<Void>> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken());
        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieProperties.name(), response.refreshToken())
                .httpOnly(refreshTokenCookieProperties.httpOnly())
                .secure(refreshTokenCookieProperties.secure())
                .sameSite(refreshTokenCookieProperties.sameSite())
                .path(refreshTokenCookieProperties.path())
                .maxAge(refreshTokenCookieProperties.maxAge())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(BaseResponse.from(StatusType.OK));
    }
}
