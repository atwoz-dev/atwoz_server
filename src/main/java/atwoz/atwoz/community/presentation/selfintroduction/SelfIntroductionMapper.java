package atwoz.atwoz.community.presentation.selfintroduction;

import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchRequest;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionSearchCondition;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.query.member.AgeConverter;

public class SelfIntroductionMapper {
    static SelfIntroductionSearchCondition toSelfIntroductionSearchCondition(SelfIntroductionSearchRequest selfIntroductionSearchRequest) {
        return new SelfIntroductionSearchCondition(
                selfIntroductionSearchRequest.preferredCities(),
                AgeConverter.toYearOfBirth(selfIntroductionSearchRequest.toAge()),
                AgeConverter.toYearOfBirth(selfIntroductionSearchRequest.fromAge()),
                Gender.from(selfIntroductionSearchRequest.gender())
        );
    }
}
