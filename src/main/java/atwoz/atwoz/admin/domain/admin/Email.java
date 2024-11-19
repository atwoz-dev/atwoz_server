package atwoz.atwoz.admin.domain.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
@EqualsAndHashCode
public class Email {

    private static final String EMAIL_REGEX = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(name = "email")
    private final String address;

    private Email(String address) {
        if (!isValidEmail(address)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 주소 형식입니다: " + address);
        }
        this.address = address;
    }

    public static Email of(String address) {
        return new Email(address);
    }

    private boolean isValidEmail(String address) {
        return address == null || !EMAIL_PATTERN.matcher(address).matches();
    }
}