package atwoz.atwoz.payment.command.domain.order;

import atwoz.atwoz.payment.command.infra.order.TransactionInfo;

public interface TokenParser {
    TransactionInfo parseToTransactionInfo(String signedTransactionInfo);
}
