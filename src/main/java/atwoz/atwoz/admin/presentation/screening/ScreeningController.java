package atwoz.atwoz.admin.presentation.screening;

import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningApproveRequest;
import atwoz.atwoz.admin.presentation.screening.dto.ScreeningRejectRequest;
import atwoz.atwoz.admin.query.*;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/admin/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;
    private final ScreeningQueryRepository screeningQueryRepository;
    private final ScreeningDetailQueryRepository screeningDetailQueryRepository;

    @PostMapping("/approve")
    public ResponseEntity<BaseResponse<Void>> approve(
            @Valid @RequestBody ScreeningApproveRequest request,
            @AuthPrincipal AuthContext authContext
    ) {
        screeningService.approve(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PostMapping("/reject")
    public ResponseEntity<BaseResponse<Void>> reject(
            @Valid @RequestBody ScreeningRejectRequest request,
            @AuthPrincipal AuthContext authContext
    ) {
        screeningService.reject(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ScreeningView>>> getScreenings(
            @Valid @ModelAttribute ScreeningSearchCondition condition,
            Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(OK, screeningQueryRepository.findScreenings(condition, pageable)));
    }

    @GetMapping("/{screeningId}")
    public ResponseEntity<BaseResponse<ScreeningDetailView>> getScreeningDetail(@PathVariable Long screeningId) {
        return ResponseEntity.ok(BaseResponse.of(OK, screeningDetailQueryRepository.findById(screeningId)));
    }
}
