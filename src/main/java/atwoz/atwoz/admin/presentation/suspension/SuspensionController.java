package atwoz.atwoz.admin.presentation.suspension;

import atwoz.atwoz.admin.command.application.suspension.SuspendRequest;
import atwoz.atwoz.admin.command.application.suspension.SuspensionService;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "관리자 회원 정지 API")
@RestController
@RequestMapping("/admin/suspensions")
@RequiredArgsConstructor
public class SuspensionController {

    private final SuspensionService suspensionService;

    @Operation(description = "회원 정지 상태 변경")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> updateStatus(
        @Valid @RequestBody SuspendRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        suspensionService.updateStatusByAdmin(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
