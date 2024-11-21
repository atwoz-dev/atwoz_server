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
public class Name {

    private static final String NAME_REGEX = "^[a-zA-Z0-9]{1,10}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    @Column(name = "name")
    private final String value;

    private Name(String value) {
        if (!isValidName(value)) {
            throw new InvalidNameException("이름은 문자와 숫자만 포함해야하며, 최대 10자까지 설정 가능합니다.");
        }
        this.value = value;
    }

    public static Name of(String value) {
        return new Name(value);
    }

    private boolean isValidName(String value) {
        return value != null && NAME_PATTERN.matcher(value).matches();
    }
}
