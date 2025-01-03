package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.exception.InvalidMemberEnumValueException;

public enum HighestEducation {
    SEOUL_4_YEAR("서울 4년제"),
    LOCAL_4_YEAR("지방 4년제"),
    JUNIOR_COLLEGE("전문대"),
    OVERSEAS_UNIVERSITY("해외대"),
    MASTER("석사"),
    DOCTORATE("박사"),
    LAW_SCHOOL("로스쿨"),
    HIGH_SCHOOL_GRADUATE("고등학교 졸업"),
    OTHER("기타");

    private String description;

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
