package atwoz.atwoz.payment.command.application.heartpurchaseoption;

import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseAmount;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.Price;
import atwoz.atwoz.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartPurchaseOptionService {
    private final HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    public void create(HeartPurchaseOptionCreateRequest request) {
        HeartPurchaseAmount heartAmount = HeartPurchaseAmount.from(request.heartAmount());
        Price price = Price.from(request.price());
        HeartPurchaseOption heartPurchaseOption = HeartPurchaseOption.of(
            heartAmount,
            price,
            request.productId(),
            request.name()
        );
        heartPurchaseOptionCommandRepository.save(heartPurchaseOption);
    }
}
