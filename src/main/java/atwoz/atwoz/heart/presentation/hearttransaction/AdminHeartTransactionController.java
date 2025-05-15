package atwoz.atwoz.heart.presentation.hearttransaction;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.heart.query.hearttransaction.HeartTransactionQueryRepository;
import atwoz.atwoz.heart.query.hearttransaction.condition.HeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/heart-transactions")
public class AdminHeartTransactionController {
    private final HeartTransactionQueryRepository heartTransactionQueryRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<HeartTransactionView>>> getPage(
        @ModelAttribute HeartTransactionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        Page<HeartTransactionView> page = heartTransactionQueryRepository.findPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }
}
