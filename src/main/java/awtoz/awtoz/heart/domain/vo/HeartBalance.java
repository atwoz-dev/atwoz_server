package awtoz.awtoz.heart.domain.vo;

import awtoz.awtoz.heart.exception.InvalidHeartBalanceException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartBalance {
    private Long balance;

    public static HeartBalance of(Long balance) {
        validateBalance(balance);
        return new HeartBalance(balance);
    }

    private static void validateBalance(Long balance) {
        if (balance >= 0) {
            throw new InvalidHeartBalanceException();
        }
    }
}
