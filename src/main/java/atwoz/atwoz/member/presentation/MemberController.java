package atwoz.atwoz.member.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> updateProfile(@RequestBody MemberProfileUpdateRequest request, @AuthPrincipal AuthContext authContext) {
        MemberProfileResponse response = memberService.updateMember(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMyProfile(@AuthPrincipal AuthContext authContext) {
        MemberProfileResponse response = memberService.getProfile(authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @PostMapping("/profile/dormant")
    public ResponseEntity<BaseResponse<Void>> transitionToDormant(@AuthPrincipal AuthContext authContext) {
        memberService.transitionToDormant(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/profile/contact")
    public ResponseEntity<BaseResponse<MemberContactResponse>> getMyContact(@AuthPrincipal AuthContext authContext) {
        MemberContactResponse response = memberService.getContacts(authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @PatchMapping("/profile/contact/kakao")
    public ResponseEntity<BaseResponse<Void>> updateKakaoId(@AuthPrincipal AuthContext authContext, @RequestBody String kakaoId) {
        memberService.updateKakaoId(authContext.getId(), kakaoId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/profile/contact/phone")
    public ResponseEntity<BaseResponse<Void>> updatePhoneNumber(@AuthPrincipal AuthContext authContext, @RequestBody String phone) {
        memberService.updatePhoneNumber(authContext.getId(), phone);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
