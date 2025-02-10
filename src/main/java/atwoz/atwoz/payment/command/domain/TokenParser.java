package atwoz.atwoz.payment.command.domain;

import atwoz.atwoz.payment.command.infra.TransactionInfo;

public interface TokenParser {
    TransactionInfo parseToTransactionInfo(String signedTransactionInfo);
}
