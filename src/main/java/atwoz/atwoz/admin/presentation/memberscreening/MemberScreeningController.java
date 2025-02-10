package atwoz.atwoz.admin.presentation.memberscreening;

import atwoz.atwoz.admin.command.application.memberscreening.MemberScreeningService;
import atwoz.atwoz.admin.command.application.memberscreening.dto.MemberScreeningApproveRequest;
import atwoz.atwoz.admin.command.application.memberscreening.dto.MemberScreeningRejectRequest;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/screenings")
@RequiredArgsConstructor
public class MemberScreeningController {

    private final MemberScreeningService memberScreeningService;

    @PostMapping("/approve")
    public ResponseEntity<BaseResponse<Void>> approve(
            @Valid @RequestBody MemberScreeningApproveRequest request,
            @AuthPrincipal AuthContext authContext
    ) {
        memberScreeningService.approve(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PostMapping("/reject")
    public ResponseEntity<BaseResponse<Void>> reject(
            @Valid @RequestBody MemberScreeningRejectRequest request,
            @AuthPrincipal AuthContext authContext
    ) {
        memberScreeningService.reject(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
