package deepple.deepple.heart.query.hearttransaction.view;

import java.util.List;

public record HeartTransactionViews(
    List<HeartTransactionView> transactions,
    boolean hasMore
) {
}
