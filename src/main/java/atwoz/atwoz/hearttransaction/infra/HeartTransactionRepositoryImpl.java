package atwoz.atwoz.hearttransaction.infra;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.HeartTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HeartTransactionRepositoryImpl implements HeartTransactionRepository {
    private final HeartTransactionJpaRepository heartTransactionJpaRepository;

    @Override
    public HeartTransaction save(HeartTransaction heartTransaction) {
        return heartTransactionJpaRepository.save(heartTransaction);
    }
}
