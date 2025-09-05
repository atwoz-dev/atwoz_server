package atwoz.atwoz.datingexam.adapter.webapi;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.provided.DatingExamFinder;
import atwoz.atwoz.datingexam.application.provided.DatingExamSubmitter;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "연애 모의고사 API")
@RestController
@RequestMapping("/dating-exam")
@RequiredArgsConstructor
public class DatingExamApi {
    private final DatingExamSubmitter datingExamSubmitter;
    private final DatingExamFinder datingExamFinder;

    @Operation(summary = "과목 답안 제출 API")
    @PostMapping("/submit")
    public ResponseEntity<BaseResponse<Void>> submitRequiredExam(
        @RequestBody @Valid DatingExamSubmitRequest datingExamSubmitRequest,
        @AuthPrincipal AuthContext authContext
    ) {
        datingExamSubmitter.submitSubject(datingExamSubmitRequest, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "필수 과목 정보 조회 API")
    @GetMapping("/required")
    public ResponseEntity<BaseResponse<DatingExamInfoResponse>> getRequiredExamInfo(
        @AuthPrincipal AuthContext authContext
    ) {
        final DatingExamInfoResponse requiredExamInfo = datingExamFinder.findRequiredExamInfo();
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, requiredExamInfo));
    }

    @Operation(summary = "선택 과목 정보 조회 API")
    @GetMapping("/optional")
    public ResponseEntity<BaseResponse<DatingExamInfoResponse>> getOptionalExamInfo(
        @AuthPrincipal AuthContext authContext
    ) {
        final DatingExamInfoResponse optionalExamInfo = datingExamFinder.findOptionalExamInfo();
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, optionalExamInfo));
    }
}
