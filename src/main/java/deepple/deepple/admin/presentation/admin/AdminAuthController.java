package deepple.deepple.admin.presentation.admin;

import deepple.deepple.admin.command.application.admin.AdminAuthService;
import deepple.deepple.admin.presentation.admin.dto.AdminLoginRequest;
import deepple.deepple.admin.presentation.admin.dto.AdminLoginResponse;
import deepple.deepple.admin.presentation.admin.dto.AdminSignupRequest;
import deepple.deepple.admin.presentation.admin.dto.AdminSignupResponse;
import deepple.deepple.auth.presentation.RefreshTokenCookieProperties;
import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 인증 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;
    private final AdminAuthService adminAuthService;

    @Operation(summary = "관리자 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AdminSignupResponse>> signup(@Valid @RequestBody AdminSignupRequest request) {
        AdminSignupResponse data = adminAuthService.signup(request);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, data));
    }

    @Operation(summary = "관리자 로그인")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<Void>> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + response.accessToken());
        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieProperties.name(),
                response.refreshToken())
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

    @Operation(summary = "관리자 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
        @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        adminAuthService.logout(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieProperties.name(), "")
            .httpOnly(refreshTokenCookieProperties.httpOnly())
            .secure(refreshTokenCookieProperties.secure())
            .sameSite(refreshTokenCookieProperties.sameSite())
            .path(refreshTokenCookieProperties.path())
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(BaseResponse.from(StatusType.OK));
    }
}
