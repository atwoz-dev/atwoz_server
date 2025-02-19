package atwoz.atwoz.match.command.domain.match.vo;

import atwoz.atwoz.match.command.domain.match.exception.InvalidMessageException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
@EqualsAndHashCode
public class Message {

    @Getter
    private String value;

    public static Message from(String message) {
        return new Message(message);
    }

    private Message(@NonNull String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.trim().isEmpty()) {
            throw new InvalidMessageException(value);
        }
    }
}
