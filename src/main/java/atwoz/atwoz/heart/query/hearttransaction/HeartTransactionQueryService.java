package atwoz.atwoz.heart.query.hearttransaction;

import atwoz.atwoz.heart.query.hearttransaction.condition.HeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionView;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionViews;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeartTransactionQueryService {
    private static final int CLIENT_PAGE_SIZE = 12;

    private final HeartTransactionQueryRepository heartTransactionQueryRepository;

    public HeartTransactionViews findHeartTransactions(long memberId, HeartTransactionSearchCondition condition) {
        List<HeartTransactionView> views = heartTransactionQueryRepository.findHeartTransactions(memberId,
            condition, CLIENT_PAGE_SIZE + 1);
        boolean hasMore = views.size() > CLIENT_PAGE_SIZE;
        List<HeartTransactionView> transactions = views.stream()
            .limit(CLIENT_PAGE_SIZE)
            .toList();

        return new HeartTransactionViews(transactions, hasMore);
    }
}
