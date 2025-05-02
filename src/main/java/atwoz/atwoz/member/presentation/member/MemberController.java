package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.member.MemberContactService;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileResponse;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.query.member.MemberQueryRepository;
import atwoz.atwoz.member.query.member.view.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberContactService memberContactService;
    private final MemberProfileService memberProfileService;
    private final MemberQueryRepository memberQueryRepository;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<Void>> updateProfile(@RequestBody MemberProfileUpdateRequest request,
        @AuthPrincipal AuthContext authContext) {
        memberProfileService.updateMember(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<MemberProfileView>> getMyProfile(@AuthPrincipal AuthContext authContext) {
        MemberProfileView response = memberQueryRepository.findProfileByMemberId(authContext.getId()).orElse(null);
        if (response == null) {
            return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getOtherProfile(@AuthPrincipal AuthContext authContext,
        @PathVariable Long memberId) {
        OtherMemberProfileView profileView = memberQueryRepository.findOtherProfileByMemberId(authContext.getId(),
            memberId).orElse(null);
        List<InterviewResultView> interviewResultViews = memberQueryRepository.findInterviewsByMemberId(memberId);

        if (profileView == null) {
            return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
        }

        return ResponseEntity.ok(BaseResponse.of(StatusType.OK,
            new MemberProfileResponse(MemberMapper.toBasicInfo(profileView.basicMemberInfo()), profileView.matchInfo(),
                interviewResultViews)));
    }

    @PostMapping("/profile/dormant")
    public ResponseEntity<BaseResponse<Void>> changeToDormant(@AuthPrincipal AuthContext authContext) {
        memberProfileService.changeToDormant(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/profile/contact")
    public ResponseEntity<BaseResponse<MemberContactView>> getMyContact(@AuthPrincipal AuthContext authContext) {
        MemberContactView response = memberQueryRepository.findContactsByMemberId(authContext.getId()).orElse(null);
        if (response == null) {
            return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, response));
    }

    @PatchMapping("/profile/contact/kakao")
    public ResponseEntity<BaseResponse<Void>> updateKakaoId(@AuthPrincipal AuthContext authContext,
        @RequestBody String kakaoId) {
        memberContactService.updateKakaoId(authContext.getId(), kakaoId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/profile/contact/phone")
    public ResponseEntity<BaseResponse<Void>> updatePhoneNumber(@AuthPrincipal AuthContext authContext,
        @RequestBody String phone) {
        memberContactService.updatePhoneNumber(authContext.getId(), phone);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping("/heartbalance")
    public ResponseEntity<BaseResponse<HeartBalanceView>> getHeartBalance(@AuthPrincipal AuthContext authContext) {
        HeartBalanceView view = memberQueryRepository.findHeartBalanceByMemberId(authContext.getId());
        if (view == null) {
            return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, view));
    }
}
