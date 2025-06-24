package atwoz.atwoz.community.presentation.selfintroduction.dto;

import atwoz.atwoz.member.command.domain.member.Gender;

import java.util.List;

public record SelfIntroductionSearchCondition(
    List<String> preferredCities,
    Integer fromYearOfBirth,
    Integer toYearOfBirth,
    Gender gender
) {
}
