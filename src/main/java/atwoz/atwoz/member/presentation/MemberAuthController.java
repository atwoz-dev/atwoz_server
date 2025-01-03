package atwoz.atwoz.member.presentation;

import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import atwoz.atwoz.member.application.MemberAuthService;
import atwoz.atwoz.member.application.dto.MemberLoginResponse;
import atwoz.atwoz.member.application.dto.MemberLoginServiceDto;
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
        MemberLoginServiceDto loginServiceDto = memberAuthService.login(phoneNumber);

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

    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        memberAuthService.logout(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(BaseResponse.from(StatusType.OK));
    }
}
