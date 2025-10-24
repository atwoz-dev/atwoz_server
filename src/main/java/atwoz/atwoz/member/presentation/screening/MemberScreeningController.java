package atwoz.atwoz.member.presentation.screening;

import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "멤버 심사 API")
@RestController
@RequestMapping("/members/screenings")
@RequiredArgsConstructor
public class MemberScreeningController {

    private final ScreeningService screeningService;

    @Operation(summary = "재심사 요청")
    @PostMapping("/rescreen")
    public ResponseEntity<BaseResponse<Void>> rescreen(
        @AuthPrincipal AuthContext authContext
    ) {
        screeningService.rescreen(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}