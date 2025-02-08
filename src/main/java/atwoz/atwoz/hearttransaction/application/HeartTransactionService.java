package atwoz.atwoz.hearttransaction.application;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartTransactionService {
    private final HeartTransactionRepository heartTransactionRepository;

    @Transactional
    public void createHeartPurchaseTransaction(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance) {
        createHeartTransaction(memberId, amount, missionHeartBalance, purchaseHeartBalance, TransactionType.PURCHASE);
    }

    private void createHeartTransaction(Long memberId, Long amount, Long missionHeartBalance, Long purchaseHeartBalance, TransactionType transactionType) {
        HeartBalance heartBalance = HeartBalance.of(missionHeartBalance, purchaseHeartBalance);
        HeartAmount heartAmount = HeartAmount.from(amount);
        HeartTransaction heartTransaction = HeartTransaction.of(memberId, transactionType, heartAmount, heartBalance);
        heartTransactionRepository.save(heartTransaction);
    }
}
