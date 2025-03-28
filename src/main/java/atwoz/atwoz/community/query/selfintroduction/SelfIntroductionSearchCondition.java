package atwoz.atwoz.community.query.selfintroduction;

import java.util.List;

public record SelfIntroductionSearchCondition(
        List<String> preferredRegions,
        Integer fromAge,
        Integer toAge,
        Boolean onlyAnotherGender
) {
}
