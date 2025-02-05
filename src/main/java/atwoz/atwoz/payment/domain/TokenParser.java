package atwoz.atwoz.payment.domain;

public interface TokenParser {
    TransactionInfo parseToTransactionInfo(String signedTransactionInfo);
}
