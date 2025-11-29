package deepple.deepple.member.command.domain.member;

import deepple.deepple.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Mbti {
    ESFP, ESFJ, ESTP, ESTJ,
    ENFP, ENFJ, ENTP, ENTJ,
    ISFP, ISFJ, ISTP, ISTJ,
    INFP, INFJ, INTP, INTJ;

    public static Mbti from(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Mbti.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
