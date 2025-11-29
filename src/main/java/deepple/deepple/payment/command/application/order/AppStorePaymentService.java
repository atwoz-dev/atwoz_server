package deepple.deepple.payment.command.application.order;

import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import deepple.deepple.common.event.Events;
import deepple.deepple.payment.command.application.order.exception.InvalidOrderException;
import deepple.deepple.payment.command.domain.order.event.AppStoreReceiptVerifiedEvent;
import deepple.deepple.payment.command.infra.order.AppStoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppStorePaymentService {
    private final AppStoreClient appStoreClient;

    public void verifyReceipt(String appReceipt, Long memberId) {
        JWSTransactionDecodedPayload decodedPayload = getTransactionDecodedPayload(appReceipt);
        verifyPayload(decodedPayload);
        raiseReceiptVerifiedEvent(memberId, decodedPayload);
    }

    private JWSTransactionDecodedPayload getTransactionDecodedPayload(String appReceipt) {
        return appStoreClient.getTransactionDecodedPayload(appReceipt);
    }

    private void verifyPayload(JWSTransactionDecodedPayload decodedPayload) {
        if (decodedPayload.getRevocationDate() != null) {
            throw new InvalidOrderException();
        }
    }

    private void raiseReceiptVerifiedEvent(final Long memberId,
        final JWSTransactionDecodedPayload decodedPayload) {
        Events.raise(
            AppStoreReceiptVerifiedEvent.of(
                memberId,
                decodedPayload.getTransactionId(),
                decodedPayload.getProductId(),
                decodedPayload.getQuantity()
            )
        );
    }
}
