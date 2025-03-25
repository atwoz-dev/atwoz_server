package atwoz.atwoz.member.command.domain.member.vo;

import atwoz.atwoz.member.command.domain.member.exception.InvalidYearOfBirthException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class YearOfBirth {

    @Column(name = "year_of_birth")
    private Integer value;

    public static YearOfBirth from(Integer yearOfBirth) {
        if (yearOfBirth == null) {
            return null;
        }
        return new YearOfBirth(yearOfBirth);
    }

    private YearOfBirth(Integer yearOfBirth) {
        validateYearOfBirth(yearOfBirth);
        this.value = yearOfBirth;
    }

    private void validateYearOfBirth(Integer yearOfBirth) {
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = nowYear - yearOfBirth + 1;
        if (age < 20 || age > 46) {
            throw new InvalidYearOfBirthException();
        }
    }
}
