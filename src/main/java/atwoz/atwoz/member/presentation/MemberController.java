package atwoz.atwoz.member.presentation;

import atwoz.atwoz.common.auth.context.AuthContext;
import atwoz.atwoz.common.auth.context.AuthPrincipal;
import atwoz.atwoz.common.presentation.BaseResponse;
import atwoz.atwoz.common.presentation.StatusType;
import atwoz.atwoz.member.application.MemberMapper;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<MemberProfileUpdateResponse>> updateProfile(@RequestBody MemberProfileUpdateRequest request, @AuthPrincipal AuthContext authContext) {
        Member member = memberService.updateMember(authContext.getId(), request);
        MemberProfileUpdateResponse response = MemberMapper.toMemberProfileUpdateResponse(member);
        return new ResponseEntity(BaseResponse.of(StatusType.OK, response), HttpStatus.OK);
    }
}
