package deepple.deepple.payment.command.application.heartpurchaseoption;

import deepple.deepple.payment.command.application.heartpurchaseoption.exception.HeartPurchaseOptionAlreadyExistsException;
import deepple.deepple.payment.command.application.order.exception.HeartPurchaseOptionNotFoundException;
import deepple.deepple.payment.command.domain.heartpurchaseoption.HeartPurchaseAmount;
import deepple.deepple.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import deepple.deepple.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import deepple.deepple.payment.command.domain.heartpurchaseoption.Price;
import deepple.deepple.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
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
        if (heartPurchaseOptionCommandRepository.existsByProductIdAndDeletedAtIsNull(request.productId())) {
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
        final HeartPurchaseOption heartPurchaseOption = heartPurchaseOptionCommandRepository.findById(id).orElseThrow(
            () -> new HeartPurchaseOptionNotFoundException("해당 id의 하트 구매 옵션이 존재하지 않습니다." + id)
        );
        if (heartPurchaseOption.isDeleted()) {
            throw new HeartPurchaseOptionNotFoundException("이미 삭제된 하트 구매 옵션입니다." + id);
        }
    }
}
