package atwoz.atwoz.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class PhoneNumber {

    private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Column(name = "phone_number")
    private final String value;

    private PhoneNumber(String value) {
        if (!isValidPhoneNumber(value)) {
            throw new InvalidPhoneNumberException("유효하지 않은 전화번호 형식입니다.");
        }
        this.value = value;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    private boolean isValidPhoneNumber(String value) {
        return value != null && PHONE_NUMBER_PATTERN.matcher(value).matches();
    }
}
