package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Region;

import java.util.List;

public record SelfIntroductionSearchCondition(
        List<Region> preferredRegions,
        Integer fromYearOfBirth,
        Integer toYearOfBirth,
        Gender gender
) {
}
