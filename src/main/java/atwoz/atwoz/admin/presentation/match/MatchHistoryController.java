package atwoz.atwoz.admin.presentation.match;

import atwoz.atwoz.admin.query.match.MatchHistoryQueryRepository;
import atwoz.atwoz.admin.query.match.MatchHistorySearchCondition;
import atwoz.atwoz.admin.query.match.MatchHistoryView;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "관리자 페이지 매치 내역 관리 API")
@RestController
@RequestMapping("/admin/matches")
@RequiredArgsConstructor
public class MatchHistoryController {

    private final MatchHistoryQueryRepository matchHistoryQueryRepository;

    @Operation(summary = "매치 내역 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<MatchHistoryView>>> getMatchHistories(
        @Valid @ModelAttribute MatchHistorySearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(
            BaseResponse.of(OK, matchHistoryQueryRepository.findMatchHistories(condition, pageable))
        );
    }
}