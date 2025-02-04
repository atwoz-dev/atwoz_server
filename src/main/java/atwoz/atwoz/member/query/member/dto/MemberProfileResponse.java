package atwoz.atwoz.member.query.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberProfileResponse {
    private String nickname;
    private Integer age;
    private String gender;
    private Integer height;
    private String job;
    private List<String> hobbies;
    private String mbti;
    private String region;
    private String smokingStatus;
    private String drinkingStatus;
    private String highestEducation;
    private String religion;

    @QueryProjection
    public MemberProfileResponse(String nickname, Integer age, String gender, Integer height, String job, List<String> hobbies, String mbti, String region, String smokingStatus, String drinkingStatus, String highestEducation, String religion) {
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.job = job;
        this.hobbies = hobbies;
        this.mbti = mbti;
        this.region = region;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.highestEducation = highestEducation;
        this.religion = religion;
    }
}
