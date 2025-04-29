package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Job {
    RESEARCH_AND_ENGINEERING("연구개발/엔지니어"),
    SELF_EMPLOYMENT("개인사업/자영업"),
    SALES("영업/판매"),
    MANAGEMENT_AND_PLANNING("경영/기획"),
    STUDYING_FOR_FUTURE("미래를 위한 공부중"),
    JOB_SEARCHING("취업 준비중"),
    EDUCATION("교육"),
    ARTS_AND_SPORTS("예술/체육"),
    FOOD_SERVICE("요식업"),
    MEDICAL_AND_HEALTH("의료/보건"),
    MECHANICAL_AND_CONSTRUCTION("기계/건설"),
    DESIGN("디자인"),
    MARKETING_AND_ADVERTISING("마케팅/광고"),
    TRADE_AND_DISTRIBUTION("무역/유통"),
    MEDIA_AND_ENTERTAINMENT("방송언론/연애"),
    LEGAL_AND_PUBLIC("법률/공공"),
    PRODUCTION_AND_MANUFACTURING("생산/제조"),
    CUSTOMER_SERVICE("서비스"),
    TRAVEL_AND_TRANSPORT("여행/운송"),
    OTHERS("기타");

    private final String description;

    /**
     * Initializes a Job enum constant with the specified Korean description.
     *
     * @param description the Korean description for the job category
     */
    Job(String description) {
        this.description = description;
    }

    /**
     * Returns the {@code Job} enum constant corresponding to the given string value.
     *
     * @param value the name of the job category to convert; case-insensitive
     * @return the matching {@code Job} enum constant, or {@code null} if the input is {@code null}
     * @throws InvalidMemberEnumValueException if the input does not match any job category
     */
    public static Job from(String value) {
        if (value == null) return null;
        try {
            return Job.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }

}
