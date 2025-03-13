package atwoz.atwoz.member.command.domain.introduction.vo;

import atwoz.atwoz.member.command.domain.introduction.exception.InvalidAgeRangeException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Embeddable
public class AgeRange {
    private static final Integer MIN_VALUE = 20;
    private static final Integer MAX_VALUE = 47;

    private final Integer minAge;
    private final Integer maxAge;

    protected AgeRange() {
        this.minAge = MIN_VALUE;
        this.maxAge = MAX_VALUE;
    }

    public static AgeRange of(Integer minAge, Integer maxAge) {
        return new AgeRange(minAge, maxAge);
    }

    private AgeRange(@NonNull Integer minAge, @NonNull Integer maxAge) {
        validateAgeRange(minAge, maxAge);
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    private void validateAgeRange(Integer minAge, Integer maxAge) {
        if (minAge < MIN_VALUE || maxAge > MAX_VALUE) {
            throw new InvalidAgeRangeException();
        }
        if (minAge > maxAge) {
            throw new InvalidAgeRangeException();
        }
    }
}
