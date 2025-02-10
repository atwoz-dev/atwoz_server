package atwoz.atwoz.hearttransaction.application;

import atwoz.atwoz.hearttransaction.command.application.HeartTransactionService;
import atwoz.atwoz.hearttransaction.command.domain.HeartTransactionCommandRepository;
import atwoz.atwoz.hearttransaction.command.domain.vo.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HeartTransactionServiceTest {
    @Mock
    private HeartTransactionCommandRepository heartTransactionCommandRepository;

    @InjectMocks
    private HeartTransactionService heartTransactionService;

    @Test
    @DisplayName("하트 구매 트랜잭션을 생성합니다.")
    void createHeartPurchaseTransaction() {
        // given
        Long memberId = 1L;
        Long amount = 100L;
        Long missionHeartBalance = 100L;
        Long purchaseHeartBalance = 100L;

        // when
        heartTransactionService.createHeartPurchaseTransaction(memberId, amount, missionHeartBalance, purchaseHeartBalance);

        // then
        verify(heartTransactionCommandRepository).save(argThat(heartTransaction ->
                heartTransaction.getMemberId().equals(memberId) &&
                        heartTransaction.getTransactionType() == TransactionType.PURCHASE &&
                        heartTransaction.getHeartAmount().getAmount().equals(amount) &&
                        heartTransaction.getHeartBalance().getMissionHeartBalance().equals(missionHeartBalance) &&
                        heartTransaction.getHeartBalance().getPurchaseHeartBalance().equals(purchaseHeartBalance)
        ));
    }
}