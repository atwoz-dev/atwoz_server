package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.auth.presentation.RefreshTokenCookieProperties;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.MemberAuthService;
import atwoz.atwoz.member.command.application.member.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.presentation.member.dto.MemberCodeRequest;
import atwoz.atwoz.member.presentation.member.dto.MemberLoginRequest;
import atwoz.atwoz.member.presentation.member.dto.MemberLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberAuthController {

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;
    private final MemberAuthService memberAuthService;

    @Operation(summary = "멤버 로그인 및 회원가입")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<MemberLoginResponse>> login(@Valid @RequestBody MemberLoginRequest request) {
        MemberLoginServiceDto loginServiceDto = memberAuthService.login(request.phoneNumber(), request.code());

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginServiceDto.refreshToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(60 * 60 * 24 * 7 * 4)
            .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        MemberLoginResponse loginResponse = MemberDtoMapper.toMemberLoginResponse(loginServiceDto);

        return ResponseEntity.ok()
            .headers(headers)
            .body(BaseResponse.of(StatusType.OK, loginResponse));
    }

    @Operation(summary = "멤버 로그아웃")
    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
        @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        memberAuthService.logout(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie deleteCookie = ResponseCookie.from(refreshTokenCookieProperties.name(), "")
            .httpOnly(refreshTokenCookieProperties.httpOnly())
            .secure(refreshTokenCookieProperties.secure())
            .sameSite(refreshTokenCookieProperties.sameSite())
            .path(refreshTokenCookieProperties.path())
            .maxAge(refreshTokenCookieProperties.maxAge())
            .build();
        headers.add(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok()
            .headers(headers)
            .body(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/code")
    public ResponseEntity<BaseResponse<Void>> getCode(@ModelAttribute @Valid MemberCodeRequest request) {
        memberAuthService.sendAuthCode(request.phoneNumber());
        return ResponseEntity.ok()
            .body(BaseResponse.from(StatusType.OK));
    }
}
