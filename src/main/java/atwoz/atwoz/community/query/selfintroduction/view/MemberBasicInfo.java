package atwoz.atwoz.community.query.selfintroduction.view;

import atwoz.atwoz.member.query.member.AgeConverter;

import java.util.List;

public record MemberBasicInfo(
        Long memberId,
        String nickname,
        Integer age,
        String profileImageUrl,
        String city,
        String district,
        String mbti,
        List<String> hobbies
){
    public MemberBasicInfo {
        age = AgeConverter.toAge(age);
    }
}
