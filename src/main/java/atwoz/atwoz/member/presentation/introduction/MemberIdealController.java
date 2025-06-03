package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.MemberIdealService;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIdealUpdateRequest;
import atwoz.atwoz.member.query.introduction.application.MemberIdealView;
import atwoz.atwoz.member.query.introduction.intra.MemberIdealQueryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 이상형 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/ideal")
public class MemberIdealController {
    private final MemberIdealService idealService;
    private final MemberIdealQueryRepository memberIdealQueryRepository;

    @Operation(summary = "멤버 이상형 설정 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<MemberIdealView>> getMemberIdeal(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        MemberIdealView memberIdealView = memberIdealQueryRepository.findMemberIdealByMemberId(memberId)
            .orElseThrow(MemberIdealNotFoundException::new);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, memberIdealView));
    }

    @Operation(summary = "멤버 이상형 설정 수정")
    @PatchMapping
    public ResponseEntity<BaseResponse<Void>> updateIdeal(@Valid @RequestBody MemberIdealUpdateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        idealService.update(request, memberId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
