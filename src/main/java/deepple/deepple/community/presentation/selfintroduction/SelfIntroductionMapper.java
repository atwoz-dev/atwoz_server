package deepple.deepple.community.presentation.selfintroduction;

import deepple.deepple.community.presentation.selfintroduction.dto.SelfIntroductionSearchCondition;
import deepple.deepple.community.presentation.selfintroduction.dto.SelfIntroductionSearchRequest;
import deepple.deepple.member.command.domain.member.Gender;
import deepple.deepple.member.query.member.AgeConverter;

public class SelfIntroductionMapper {
    public static SelfIntroductionSearchCondition toSelfIntroductionSearchCondition(
        SelfIntroductionSearchRequest selfIntroductionSearchRequest) {
        return new SelfIntroductionSearchCondition(
            selfIntroductionSearchRequest.preferredCities(),
            AgeConverter.toYearOfBirth(selfIntroductionSearchRequest.toAge()),
            AgeConverter.toYearOfBirth(selfIntroductionSearchRequest.fromAge()),
            Gender.from(selfIntroductionSearchRequest.gender())
        );
    }
}
