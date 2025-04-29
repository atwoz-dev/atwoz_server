package atwoz.atwoz.community.query.selfintroduction.view;

import atwoz.atwoz.member.query.member.AgeConverter;

import java.util.List;
import java.util.Set;

public record MemberBasicInfo(
        Long memberId,
        String nickname,
        Integer age,
        String profileImageUrl,
        String city,
        String district,
        String mbti,
        Set<String> hobbies
){
    public MemberBasicInfo {
        age = AgeConverter.toAge(age);
    }
}
