package deepple.deepple.community.presentation.selfintroduction.dto;

import deepple.deepple.member.command.domain.member.Gender;

import java.util.List;

public record SelfIntroductionSearchCondition(
    List<String> preferredCities,
    Integer fromYearOfBirth,
    Integer toYearOfBirth,
    Gender gender
) {
}
