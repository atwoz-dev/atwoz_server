package atwoz.atwoz.community.presentation.selfintroduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.community.command.application.selfintroduction.SelfIntroductionService;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchCondition;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchRequest;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionQueryRepository;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "셀프 소개 API")
@RestController
@RequestMapping("/self-introduction")
@RequiredArgsConstructor
public class SelfIntroductionController {

    private final SelfIntroductionService selfIntroductionService;
    private final SelfIntroductionQueryRepository selfIntroductionQueryRepository;

    @Operation(summary = "셀프 소개 작성 API")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> write(@RequestBody SelfIntroductionWriteRequest request,
        @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.write(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "셀프 소개 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> update(@PathVariable Long id,
        @RequestBody SelfIntroductionWriteRequest request, @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.update(request, authContext.getId(), id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "셀프 소개 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, @AuthPrincipal AuthContext authContext) {
        selfIntroductionService.delete(id, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "셀프 소개 목록 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse<List<SelfIntroductionSummaryView>>> getIntroductions(
        @AuthPrincipal AuthContext authContext,
        @ParameterObject @ModelAttribute SelfIntroductionSearchRequest searchRequest
    ) {
        SelfIntroductionSearchCondition searchCondition = SelfIntroductionMapper.toSelfIntroductionSearchCondition(
            searchRequest);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK,
            selfIntroductionQueryRepository.findSelfIntroductions(searchCondition, searchRequest.lastId(),
                authContext.getId())));
    }

    @Operation(summary = "셀프 소개 상세 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SelfIntroductionView>> getIntroduction(@PathVariable Long id,
        @AuthPrincipal AuthContext authContext) {
        SelfIntroductionView view = selfIntroductionQueryRepository.findSelfIntroductionByIdWithMemberId(id,
            authContext.getId()).orElse(null);
        if (view == null) {
            return ResponseEntity.status(404)
                .body(BaseResponse.from(StatusType.NOT_FOUND));
        }

        return ResponseEntity.ok().body(BaseResponse.of(StatusType.OK, view));
    }
}
