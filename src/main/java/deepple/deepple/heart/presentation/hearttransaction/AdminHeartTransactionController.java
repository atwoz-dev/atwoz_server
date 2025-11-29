package deepple.deepple.heart.presentation.hearttransaction;

import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.heart.query.hearttransaction.AdminHeartTransactionQueryRepository;
import deepple.deepple.heart.query.hearttransaction.condition.AdminHeartTransactionSearchCondition;
import deepple.deepple.heart.query.hearttransaction.view.AdminHeartTransactionView;
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
    private final AdminHeartTransactionQueryRepository adminHeartTransactionQueryRepository;

    @Operation(summary = "하트 트랜잭션 목록 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdminHeartTransactionView>>> getPage(
        @ModelAttribute AdminHeartTransactionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<AdminHeartTransactionView> page = adminHeartTransactionQueryRepository.findPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }
}
