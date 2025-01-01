package atwoz.atwoz.member.presentation;

import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import atwoz.atwoz.member.application.MemberAuthService;
import atwoz.atwoz.member.application.dto.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/auth")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<MemberLoginResponse>> login(@RequestBody String phoneNumber) {
        MemberLoginResponse loginResponse = memberAuthService.login(phoneNumber);

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 7 * 4)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(BaseResponse.of(StatusType.OK, loginResponse));
    }

    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@CookieValue(value = "refresh_token", required = false) String refresh_token) {
        memberAuthService.addTokenToBlackList(refresh_token);

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();
        headers.add(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(BaseResponse.from(StatusType.OK));
    }
}
