package atwoz.atwoz.common.vo;

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
public class Name {

    private static final String NAME_REGEX = "^[a-zA-Z0-9가-\uD7AF]{1,10}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    @Column(name = "name")
    private final String value;

    private Name(String value) {
        validateName(value);
        this.value = value;
    }

    public static Name from(String value) {
        return new Name(value);
    }

    private void validateName(String value) {
        if (value == null || !NAME_PATTERN.matcher(value).matches()) {
            throw new InvalidNameException(value);
        }
    }
}
