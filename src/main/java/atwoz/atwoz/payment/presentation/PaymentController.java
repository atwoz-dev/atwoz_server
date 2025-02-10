package atwoz.atwoz.payment.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.application.AppStorePaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentController {
    private final AppStorePaymentService appStorePaymentService;

    @PostMapping("/app-store/verify-receipt")
    public ResponseEntity<BaseResponse<Void>> verifyReceipt(@RequestBody String receiptToken, @AuthPrincipal AuthContext authContext) {
        appStorePaymentService.verifyReceipt(receiptToken, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
