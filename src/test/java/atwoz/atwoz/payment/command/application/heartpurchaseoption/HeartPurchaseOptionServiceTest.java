package atwoz.atwoz.payment.command.application.heartpurchaseoption;

import atwoz.atwoz.payment.command.application.heartpurchaseoption.exception.HeartPurchaseOptionAlreadyExistsException;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOptionCommandRepository;
import atwoz.atwoz.payment.presentation.heartpurchaseoption.dto.HeartPurchaseOptionCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeartPurchaseOptionServiceTest {

    @InjectMocks
    private HeartPurchaseOptionService heartPurchaseOptionService;

    @Mock
    private HeartPurchaseOptionCommandRepository heartPurchaseOptionCommandRepository;

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateMethodTest {

        String productId = "productId";
        Long heartAmount = 100L;
        Long price = 1000L;
        String name = "name";

        @Test
        @DisplayName("이미 존재하는 productId로 생성 요청 시 예외 발생")
        void throwExceptionWhenProductIdAlreadyExists() {
            // Given
            HeartPurchaseOptionCreateRequest request = new HeartPurchaseOptionCreateRequest(
                heartAmount,
                price,
                productId,
                name
            );
            when(heartPurchaseOptionCommandRepository.existsByProductId(request.productId())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> heartPurchaseOptionService.create(request))
                .isInstanceOf(HeartPurchaseOptionAlreadyExistsException.class);
            verify(heartPurchaseOptionCommandRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 productId로 생성 요청 시 정상 처리")
        void createHeartPurchaseOption() {
            // Given
            HeartPurchaseOptionCreateRequest request = new HeartPurchaseOptionCreateRequest(
                heartAmount,
                price,
                productId,
                name
            );
            when(heartPurchaseOptionCommandRepository.existsByProductId(request.productId())).thenReturn(false);

            // When
            heartPurchaseOptionService.create(request);

            // Then
            verify(heartPurchaseOptionCommandRepository).save(argThat(heartPurchaseOption ->
                heartPurchaseOption.getHeartAmount().equals(heartAmount) &&
                    heartPurchaseOption.getPrice().getValue().equals(price) &&
                    heartPurchaseOption.getProductId().equals(productId) &&
                    heartPurchaseOption.getName().equals(name)
            ));
        }
    }
}