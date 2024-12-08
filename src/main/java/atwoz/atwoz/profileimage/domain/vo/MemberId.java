package atwoz.atwoz.profileimage.domain.vo;

import atwoz.atwoz.profileimage.exception.InvalidMemberIdException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class MemberId {

    @Column(name = "member_id")
    private final Long value;

    private MemberId(Long value) {
        validateMemberId(value);
        this.value = value;
    }

    private void validateMemberId(Long value) {
        if (value == null) {
            throw new InvalidMemberIdException();
        }
    }

    public static MemberId from(Long value) {
        return new MemberId(value);
    }
}
