package atwoz.atwoz.payment.presentation.heartpurchaseoption;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.heartpurchaseoption.HeartPurchaseOptionService;
import atwoz.atwoz.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
import atwoz.atwoz.payment.query.heartpurchaseoption.HeartPurchaseOptionQueryRepository;
import atwoz.atwoz.payment.query.heartpurchaseoption.condition.HeartPurchaseOptionSearchCondition;
import atwoz.atwoz.payment.query.heartpurchaseoption.view.HeartPurchaseOptionView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/heart-purchase-options")
public class AdminHeartPurchaseOptionController {
    private final HeartPurchaseOptionService heartPurchaseOptionService;
    private final HeartPurchaseOptionQueryRepository heartPurchaseOptionQueryRepository;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody HeartPurchaseOptionCreateRequest request) {
        heartPurchaseOptionService.create(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        heartPurchaseOptionService.delete(id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<HeartPurchaseOptionView>>> getPage(
        @ModelAttribute HeartPurchaseOptionSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable) {
        Page<HeartPurchaseOptionView> page = heartPurchaseOptionQueryRepository.findPage(condition, pageable);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, page));
    }
}
