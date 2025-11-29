package deepple.deepple.payment.presentation.order;

import deepple.deepple.auth.presentation.AuthContext;
import deepple.deepple.auth.presentation.AuthPrincipal;
import deepple.deepple.common.enums.StatusType;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.payment.command.application.order.AppStorePaymentService;
import deepple.deepple.payment.presentation.order.dto.VerifyReceiptRequest;
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
