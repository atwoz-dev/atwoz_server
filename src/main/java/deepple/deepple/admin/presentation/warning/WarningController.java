package deepple.deepple.admin.presentation.warning;

import deepple.deepple.admin.command.application.warning.WarningService;
import deepple.deepple.auth.presentation.AuthContext;
import deepple.deepple.auth.presentation.AuthPrincipal;
import deepple.deepple.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static deepple.deepple.common.enums.StatusType.OK;

@Tag(name = "관리자 경고 API")
@RestController
@RequestMapping("/admin/warnings")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @Operation(summary = "경고 부여")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> issue(
        @AuthPrincipal AuthContext authContext,
        @Valid @RequestBody WarningCreateRequest request
    ) {
        warningService.issue(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
