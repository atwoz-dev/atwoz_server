package atwoz.atwoz.admin.presentation.screening;

import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import atwoz.atwoz.admin.query.screening.*;
import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "관리자 페이지 심사 관리 API")
@RestController
@RequestMapping("/admin/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;
    private final ScreeningQueryRepository screeningQueryRepository;
    private final ScreeningDetailQueryRepository screeningDetailQueryRepository;

    @Operation(summary = "심사 목록 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<ScreeningView>>> getScreenings(
        @Valid @ModelAttribute ScreeningSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(OK, screeningQueryRepository.findScreenings(condition, pageable)));
    }

    @Operation(summary = "심사 상세 조회")
    @GetMapping("/{screeningId}")
    public ResponseEntity<BaseResponse<ScreeningDetailView>> getScreeningDetail(@PathVariable long screeningId) {
        return ResponseEntity.ok(BaseResponse.of(OK, screeningDetailQueryRepository.findById(screeningId)));
    }

    @Operation(summary = "심사 승인")
    @PostMapping("/{screeningId}/approve")
    public ResponseEntity<BaseResponse<Void>> approve(
        @PathVariable long screeningId,
        @RequestBody ScreeningApproveRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        screeningService.approve(screeningId, request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "심사 거절")
    @PostMapping("/{screeningId}/reject")
    public ResponseEntity<BaseResponse<Void>> reject(
        @PathVariable long screeningId,
        @Valid @RequestBody ScreeningRejectRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        screeningService.reject(screeningId, authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "재심사")
    @PostMapping("/rescreen")
    public ResponseEntity<BaseResponse<Void>> rescreen(
        @Valid @RequestBody RescreenRequest request
    ) {
        screeningService.rescreen(request.memberId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
