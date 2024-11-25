package awtoz.awtoz.heart.domain.vo;

import awtoz.awtoz.heart.exception.InvalidHeartBalanceException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartBalance {
    @Getter
    private Long balance;

    public static HeartBalance from(Long balance) {
        validateBalanceIsGreaterThanZero(balance);
        return new HeartBalance(balance);
    }

    private static void validateBalanceIsGreaterThanZero(Long balance) {
        if (balance < 0) {
            throw new InvalidHeartBalanceException("하트 잔액은 0 이상의 값이어야 합니다. balance: " + balance);
        }
    }
}
