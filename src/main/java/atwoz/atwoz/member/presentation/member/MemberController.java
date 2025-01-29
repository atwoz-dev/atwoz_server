package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.MemberContactService;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import atwoz.atwoz.member.command.application.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.query.member.MemberQueryRepository;
import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.query.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberContactService memberContactService;
    private final MemberProfileService memberProfileService;
    private final MemberQueryRepository memberQueryRepository;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<Void>> updateProfile(@RequestBody MemberProfileUpdateRequest request, @AuthPrincipal AuthContext authContext) {
        memberProfileService.updateMember(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMyProfile(@AuthPrincipal AuthContext authContext) {
        MemberProfileResponse response = memberQueryRepository.getProfile(authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @PostMapping("/profile/dormant")
    public ResponseEntity<BaseResponse<Void>> changeToDormant(@AuthPrincipal AuthContext authContext) {
        memberProfileService.changeToDormant(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/profile/contact")
    public ResponseEntity<BaseResponse<MemberContactResponse>> getMyContact(@AuthPrincipal AuthContext authContext) {
        MemberContactResponse response = memberQueryRepository.getContacts(authContext.getId());
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @PatchMapping("/profile/contact/kakao")
    public ResponseEntity<BaseResponse<Void>> updateKakaoId(@AuthPrincipal AuthContext authContext, @RequestBody String kakaoId) {
        memberContactService.updateKakaoId(authContext.getId(), kakaoId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/profile/contact/phone")
    public ResponseEntity<BaseResponse<Void>> updatePhoneNumber(@AuthPrincipal AuthContext authContext, @RequestBody String phone) {
        memberContactService.updatePhoneNumber(authContext.getId(), phone);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
