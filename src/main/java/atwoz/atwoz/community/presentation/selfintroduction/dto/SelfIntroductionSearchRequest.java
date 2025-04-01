package atwoz.atwoz.community.presentation.selfintroduction.dto;

import atwoz.atwoz.member.command.domain.member.Region;

import java.util.List;

public record SelfIntroductionSearchRequest(
        List<Region> preferredRegions,
        Integer fromAge,
        Integer toAge,
        String gender
) {
}
