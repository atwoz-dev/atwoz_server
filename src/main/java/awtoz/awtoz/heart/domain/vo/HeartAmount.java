package awtoz.awtoz.heart.domain.vo;

import awtoz.awtoz.heart.exception.InvalidHeartAmountException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeartAmount {
    private Long amount;

    public static HeartAmount use(Long amount) {
        validateUsingAmount(amount);
        return new HeartAmount(amount);
    }

    private static void validateUsingAmount(Long amount) {
        if (amount > 0) {
            throw new InvalidHeartAmountException("사용량은 0 이하의 값이어야 합니다.");
        }
    }

    public static HeartAmount gain(Long amount) {
        validateGainingAmount(amount);
        return new HeartAmount(amount);
    }

    private static void validateGainingAmount(Long amount) {
        if (amount <= 0) {
            throw new InvalidHeartAmountException("얻는 값은 1 이상의 값이어야 합니다.");
        }
    }
}
