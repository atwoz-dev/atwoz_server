package atwoz.atwoz.admin.domain.admin;

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
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{10,20}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Column(name = "password")
    private final String value;

    private Password(String value) {
        validatePassword(value);
        this.value = value;
    }

    public static Password from(String value) {
        return new Password(value);
    }

    private void validatePassword(String value) {
        if (value == null || !PASSWORD_PATTERN.matcher(value).matches()) {
            throw new InvalidPasswordException(value);
        }
    }
}