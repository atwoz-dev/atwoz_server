package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.domain.member.exception.InvalidMemberEnumValueException;

public enum HighestEducation {
    SEOUL_FOUR_YEAR("서울 4년제"),
    LOCAL_FOUR_YEAR("지방 4년제"),
    TWO_YEAR_COLLEGE("전문대"),
    OVERSEAS_UNIVERSITY("해외대"),
    MASTERS("석사"),
    DOCTORATE("박사"),
    LAW_SCHOOL("로스쿨"),
    HIGH_SCHOOL("고등학교 졸업"),
    OTHER("기타");

    private final String description;

    HighestEducation(String description) {
        this.description = description;
    }

    public static HighestEducation from(String value) {
        if (value == null) return null;

        try {
            return HighestEducation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
