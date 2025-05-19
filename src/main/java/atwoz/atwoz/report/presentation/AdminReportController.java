package atwoz.atwoz.report.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.report.command.application.AdminReportService;
import atwoz.atwoz.report.presentation.dto.ReportApproveRequest;
import atwoz.atwoz.report.presentation.dto.ReportRejectRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final AdminReportService adminReportService;

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
}
