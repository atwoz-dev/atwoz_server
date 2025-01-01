package atwoz.atwoz.member.presentation;

import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import atwoz.atwoz.member.application.MemberAuthService;
import atwoz.atwoz.member.application.MemberMapper;
import atwoz.atwoz.member.application.dto.MemberLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<BaseResponse<MemberLoginResponse>> login(@RequestBody String phoneNumber, HttpServletResponse response) {
        MemberLoginResponse loginResponse = MemberMapper.toMemberLoginResponse(memberAuthService.login(phoneNumber));

        return ResponseEntity.ok()
                .body(BaseResponse.of(StatusType.OK, loginResponse));
    }

    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@CookieValue(value = "refresh_token", required = false) String refresh_token) {
        memberAuthService.logout(refresh_token);

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
