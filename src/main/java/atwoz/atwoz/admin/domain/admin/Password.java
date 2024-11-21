package atwoz.atwoz.admin.domain.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{10,20}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Column(name = "password")
    private final String value;

    private Password(String value) {
        if (!isValidPassword(value)) {
            throw new IllegalArgumentException("비밀번호는 10자 이상 20자 이하여야하며, 문자, 숫자, 특수문자를 포함해야 합니다.");
        }
        this.value = value;
    }

    public static Password of(String value) {
        return new Password(value);
    }

    private boolean isValidPassword(String value) {
        return value != null && PASSWORD_PATTERN.matcher(value).matches();
    }
}