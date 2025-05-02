package atwoz.atwoz.community.presentation.selfintroduction.dto;

import atwoz.atwoz.member.command.domain.member.City;

import java.util.List;

public record SelfIntroductionSearchRequest(
    List<City> preferredCities,
    Integer fromAge,
    Integer toAge,
    String gender
) {
}
