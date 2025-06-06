package atwoz.atwoz.report.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.report.command.application.ReportService;
import atwoz.atwoz.report.presentation.dto.ReportCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "신고 API")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "신고하기")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> report(@Valid @RequestBody ReportCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        reportService.report(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
