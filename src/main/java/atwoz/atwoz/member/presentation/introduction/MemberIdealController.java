package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.MemberIdealService;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIdealUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/ideal")
public class MemberIdealController {
    private final MemberIdealService idealService;

    @PatchMapping
    public ResponseEntity<BaseResponse<Void>> updateIdeal(@RequestBody MemberIdealUpdateRequest request, @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        idealService.update(request, memberId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
