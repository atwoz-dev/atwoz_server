package atwoz.atwoz.payment.presentation.order;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.order.AppStorePaymentService;
import atwoz.atwoz.payment.presentation.order.dto.VerifyReceiptRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseResponse<Void>> verifyReceipt(@Valid @RequestBody VerifyReceiptRequest request,
        @AuthPrincipal AuthContext authContext) {
        appStorePaymentService.verifyReceipt(request.appReceipt(), authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
