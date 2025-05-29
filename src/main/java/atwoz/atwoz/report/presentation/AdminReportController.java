package atwoz.atwoz.report.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.report.command.application.AdminReportService;
import atwoz.atwoz.report.command.application.exception.ReportNotFoundException;
import atwoz.atwoz.report.presentation.dto.ReportResultUpdateRequest;
import atwoz.atwoz.report.query.ReportQueryRepository;
import atwoz.atwoz.report.query.condition.ReportSearchCondition;
import atwoz.atwoz.report.query.view.ReportDetailView;
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

    @PatchMapping("/{id}/result")
    public ResponseEntity<BaseResponse<Void>> updateResult(
        @PathVariable long id,
        @Valid @RequestBody ReportResultUpdateRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        adminReportService.updateResult(id, request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ReportView>>> getPage(
        @ModelAttribute ReportSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<ReportView> page = reportQueryRepository.getPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ReportDetailView>> getReport(@PathVariable long id) {
        ReportDetailView view = reportQueryRepository.findReportDetailView(id)
            .orElseThrow(ReportNotFoundException::new);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, view));
    }
}
