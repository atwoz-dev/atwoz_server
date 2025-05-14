package atwoz.atwoz.payment.presentation.heartpurchaseoption;

import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.heartpurchaseoption.HeartPurchaseOptionService;
import atwoz.atwoz.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/heartpurchaseoption")
public class AdminHeartPurchaseOptionController {
    private final HeartPurchaseOptionService heartPurchaseOptionService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody HeartPurchaseOptionCreateRequest request) {
        heartPurchaseOptionService.create(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
