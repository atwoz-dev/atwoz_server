package atwoz.atwoz.payment.presentation.order;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.payment.command.application.order.AppStorePaymentService;
import atwoz.atwoz.payment.presentation.order.dto.VerifyReceiptRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "앱스토어 결제 영수증 인증 API")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentController {
    private final AppStorePaymentService appStorePaymentService;

    @Operation(summary = "앱스토어 결제 영수증 인증")
    @PostMapping("/app-store/verify-receipt")
    public ResponseEntity<BaseResponse<Void>> verifyReceipt(@Valid @RequestBody VerifyReceiptRequest request,
        @AuthPrincipal AuthContext authContext) {
        appStorePaymentService.verifyReceipt(request.appReceipt(), authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
