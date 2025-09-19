package atwoz.atwoz.payment.command.infra.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AppStoreTransactionResponse {

    @JsonProperty("signedTransactionInfo")
    private String signedTransactionInfo;
}