package atwoz.atwoz.heart.presentation.hearttransaction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.heart.query.hearttransaction.HeartTransactionQueryService;
import atwoz.atwoz.heart.query.hearttransaction.condition.HeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionViews;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "하트 내역 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/heart-transactions")
public class HeartTransactionController {
    private final HeartTransactionQueryService heartTransactionQueryService;

    @Operation(summary = "하트 내역 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<HeartTransactionViews>> getHeartTransactions(
        @ModelAttribute HeartTransactionSearchCondition condition,
        @AuthPrincipal AuthContext authContext
    ) {
        final HeartTransactionViews views = heartTransactionQueryService.findHeartTransactions(
            authContext.getId(), condition);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, views));
    }
}
