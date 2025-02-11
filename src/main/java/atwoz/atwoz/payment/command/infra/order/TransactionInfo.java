package atwoz.atwoz.payment.command.infra.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class TransactionInfo {
    @JsonProperty("appAccountToken")
    private String appAccountToken;

    @JsonProperty("bundleId")
    private String bundleId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("environment")
    private String environment;

    @JsonProperty("expiresDate")
    private Long expiresDate;

    @JsonProperty("inAppOwnershipType")
    private String inAppOwnershipType;

    @JsonProperty("isUpgraded")
    private Boolean isUpgraded;

    @JsonProperty("offerDiscountType")
    private String offerDiscountType;

    @JsonProperty("offerIdentifier")
    private String offerIdentifier;

    @JsonProperty("offerType")
    private String offerType;

    @JsonProperty("originalPurchaseDate")
    private Long originalPurchaseDate;

    @JsonProperty("originalTransactionId")
    private String originalTransactionId;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("productId")
    @Getter
    private String productId;

    @JsonProperty("purchaseDate")
    private Long purchaseDate;

    @JsonProperty("quantity")
    @Getter
    private Integer quantity;

    @JsonProperty("revocationDate")
    private Long revocationDate;

    @JsonProperty("revocationReason")
    private String revocationReason;

    @JsonProperty("signedDate")
    private Long signedDate;

    @JsonProperty("storefront")
    private String storefront;

    @JsonProperty("storefrontId")
    private String storefrontId;

    @JsonProperty("subscriptionGroupIdentifier")
    private String subscriptionGroupIdentifier;

    @JsonProperty("transactionId")
    @Getter
    private String transactionId;

    @JsonProperty("transactionReason")
    private String transactionReason;

    @JsonProperty("type")
    private String type;

    @JsonProperty("webOrderLineItemId")
    private String webOrderLineItemId;

    public boolean isPaid() {
        return purchaseDate != null && revocationDate == null;
    }

    public boolean isRevoked() {
        return revocationDate != null;
    }
}
