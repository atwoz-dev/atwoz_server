package deepple.deepple.admin.command.domain.admin;

import deepple.deepple.admin.command.domain.admin.exception.InvalidNameException;
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
public class Name {

    private static final String NAME_REGEX = "^[a-zA-Z0-9가-\uD7AF]{1,10}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    @Column(name = "name")
    private final String value;

    private Name(@NonNull String value) {
        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new InvalidNameException(value);
        }
        this.value = value;
    }

    public static Name from(String value) {
        return new Name(value);
    }
}
