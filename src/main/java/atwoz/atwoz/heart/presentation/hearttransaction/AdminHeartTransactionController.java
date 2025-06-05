package atwoz.atwoz.heart.presentation.hearttransaction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.heart.query.hearttransaction.HeartTransactionQueryRepository;
import atwoz.atwoz.heart.query.hearttransaction.condition.HeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 하트 트랜잭션 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/heart-transactions")
public class AdminHeartTransactionController {
    private final HeartTransactionQueryRepository heartTransactionQueryRepository;

    @Operation(summary = "하트 트랜잭션 목록 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<HeartTransactionView>>> getPage(
        @ModelAttribute HeartTransactionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<HeartTransactionView> page = heartTransactionQueryRepository.findPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }
}
