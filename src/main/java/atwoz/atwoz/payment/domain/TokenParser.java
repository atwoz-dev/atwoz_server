package atwoz.atwoz.payment.domain;

import atwoz.atwoz.payment.infra.TransactionInfo;

public interface TokenParser {
    TransactionInfo parseToTransactionInfo(String signedTransactionInfo);
}
