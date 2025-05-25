package atwoz.atwoz.admin.presentation.selfintroduction;

import atwoz.atwoz.admin.command.application.selfintroduction.AdminSelfIntroductionCommandService;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionQueryRepository;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionSearchCondition;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionView;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "어드민 셀프 소개 관리 API")
@RestController
@RequestMapping("/admin/selfintroduction")
@RequiredArgsConstructor
public class AdminSelfIntroductionController {

    private final AdminSelfIntroductionQueryRepository adminSelfIntroductionQueryRepository;
    private final AdminSelfIntroductionCommandService adminSelfIntroductionCommandService;

    @Operation(summary = "셀프 소개 목록 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdminSelfIntroductionView>>> getSelfIntroductions(
        @ModelAttribute AdminSelfIntroductionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK,
            adminSelfIntroductionQueryRepository.findSelfIntroductions(condition, pageable)));
    }

    @Operation(summary = "셀프 소개 공개 전환 API")
    @PatchMapping("/{id}/open")
    public ResponseEntity<BaseResponse<Void>> updateSelfIntroductionToOpen(@PathVariable Long id) {
        adminSelfIntroductionCommandService.convertToOpen(id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "셀프 소개 비공개 전환 API")
    @PatchMapping("/{id}/close")
    public ResponseEntity<BaseResponse<Void>> updateSelfIntroductionToClose(@PathVariable Long id) {
        adminSelfIntroductionCommandService.convertToClose(id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
