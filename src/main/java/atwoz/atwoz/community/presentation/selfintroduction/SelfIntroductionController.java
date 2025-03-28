package atwoz.atwoz.community.presentation.selfintroduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.community.command.application.selfintroduction.SelfIntroductionService;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSummaryResponse;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionSearchCondition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/self-introduction")
@RequiredArgsConstructor
public class SelfIntroductionController {

    private final SelfIntroductionService selfIntroductionService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> write(@RequestBody SelfIntroductionWriteRequest request, @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.write(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> update(@PathVariable Long id, @RequestBody SelfIntroductionWriteRequest request, @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.update(request, authContext.getId(), id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable Long id, @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.delete(id, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<SelfIntroductionSummaryResponse>>> getIntroductions(@AuthPrincipal AuthContext authContext, @ModelAttribute SelfIntroductionSearchCondition searchCondition, Pageable pageable) {

        return null;
    }
}
