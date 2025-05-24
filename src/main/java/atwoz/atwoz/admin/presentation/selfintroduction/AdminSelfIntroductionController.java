package atwoz.atwoz.admin.presentation.selfintroduction;

import atwoz.atwoz.admin.command.application.selfintroduction.AdminSelfIntroductionCommandService;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionQueryRepository;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionSearchCondition;
import atwoz.atwoz.admin.query.selfintroduction.AdminSelfIntroductionView;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/selfintroduction")
@RequiredArgsConstructor
public class AdminSelfIntroductionController {

    private final AdminSelfIntroductionQueryRepository adminSelfIntroductionQueryRepository;
    private final AdminSelfIntroductionCommandService adminSelfIntroductionCommandService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdminSelfIntroductionView>>> getSelfIntroductions(
        @ModelAttribute AdminSelfIntroductionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK,
            adminSelfIntroductionQueryRepository.findSelfIntroductions(condition, pageable)));
    }

    @PatchMapping("/{id}/open")
    public ResponseEntity<BaseResponse<Void>> updateSelfIntroductionToOpen(@PathVariable Long id) {
        adminSelfIntroductionCommandService.convertToOpen(id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<BaseResponse<Void>> updateSelfIntroductionToClose(@PathVariable Long id) {
        adminSelfIntroductionCommandService.convertToClose(id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
