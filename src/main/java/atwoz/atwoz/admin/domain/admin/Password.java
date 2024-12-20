package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.admin.domain.service.PasswordHasher;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
@Getter
@EqualsAndHashCode
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{10,20}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Column(name = "password")
    private final String hashedValue;

    public static Password fromRaw(@NonNull String rawValue, PasswordHasher hasher) {
        if (!PASSWORD_PATTERN.matcher(rawValue).matches()) {
            throw new InvalidPasswordException(rawValue);
        }
        String hashedValue = hasher.hash(rawValue);
        return new Password(hashedValue);
    }

    public static Password fromHashed(String hashedValue) {
        return new Password(hashedValue);
    }

    private Password(@NonNull String hashedValue) {
        if (hashedValue.isBlank()) {
            throw new InvalidPasswordException(hashedValue);
        }
        this.hashedValue = hashedValue;
    }
}