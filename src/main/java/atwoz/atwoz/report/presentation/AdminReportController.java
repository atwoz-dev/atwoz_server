package atwoz.atwoz.report.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.report.command.application.AdminReportService;
import atwoz.atwoz.report.presentation.dto.ReportApproveRequest;
import atwoz.atwoz.report.presentation.dto.ReportRejectRequest;
import atwoz.atwoz.report.query.ReportQueryRepository;
import atwoz.atwoz.report.query.condition.ReportSearchCondition;
import atwoz.atwoz.report.query.view.ReportView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final AdminReportService adminReportService;
    private final ReportQueryRepository reportQueryRepository;

    @PatchMapping("/{id}/approve")
    public void approve(
        @PathVariable long id,
        @Valid @RequestBody ReportApproveRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        adminReportService.approve(id, request, authContext.getId());
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable long id,
        @Valid @RequestBody ReportRejectRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        adminReportService.reject(id, request, authContext.getId());
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ReportView>>> getPage(
        @ModelAttribute ReportSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<ReportView> page = reportQueryRepository.getPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }
}
