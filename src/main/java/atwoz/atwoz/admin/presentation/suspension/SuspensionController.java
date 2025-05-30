package atwoz.atwoz.admin.presentation.suspension;

import atwoz.atwoz.admin.command.application.suspension.SuspendRequest;
import atwoz.atwoz.admin.command.application.suspension.SuspensionService;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/admin/suspensions")
@RequiredArgsConstructor
public class SuspensionController {

    private final SuspensionService suspensionService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> updateStatus(
        @Valid @RequestBody SuspendRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        suspensionService.updateStatusByAdmin(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
