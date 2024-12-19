package atwoz.atwoz.common.domain.vo;

import atwoz.atwoz.common.domain.vo.exception.InvalidPhoneNumberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@EqualsAndHashCode
public class PhoneNumber {

    private static final String PHONE_NUMBER_REGEX = "^010\\d{8}$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Column(name = "phone_number")
    private final String value;

    public static PhoneNumber from(String value) {
        return new PhoneNumber(value);
    }

    private PhoneNumber(String value) {
        if (value == null || !PHONE_NUMBER_PATTERN.matcher(value).matches()) {
            throw new InvalidPhoneNumberException(value);
        }
        this.value = value;
    }
}
