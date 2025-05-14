package atwoz.atwoz.payment.command.application.heartpurchaseoption;

import atwoz.atwoz.heart.command.application.heartusagepolicy.exception.HeartUsagePolicyNotFoundException;
import atwoz.atwoz.payment.command.application.heartpurchaseoption.exception.HeartPurchaseOptionAlreadyExistsException;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseAmount;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.Price;
import atwoz.atwoz.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartPurchaseOptionService {
    private final HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @Transactional
    public void create(HeartPurchaseOptionCreateRequest request) {
        validateCreateRequest(request);
        createHeartPurchaseOption(request);
    }

    @Transactional
    public void delete(Long id) {
        validateDeleteRequest(id);
        heartPurchaseOptionCommandRepository.deleteById(id);
    }

    private void validateCreateRequest(HeartPurchaseOptionCreateRequest request) {
        if (heartPurchaseOptionCommandRepository.existsByProductId(request.productId())) {
            throw new HeartPurchaseOptionAlreadyExistsException(request.productId());
        }
    }

    private void createHeartPurchaseOption(HeartPurchaseOptionCreateRequest request) {
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

    private void validateDeleteRequest(Long id) {
        if (!heartPurchaseOptionCommandRepository.existsById(id)) {
            throw new HeartUsagePolicyNotFoundException();
        }
    }
}
