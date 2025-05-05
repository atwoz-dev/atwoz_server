package atwoz.atwoz.member.query.member.view;

import java.util.List;
import java.util.Set;

public record ProfileInfo(
    String job,
    String highestEducation,
    String city,
    String district,
    String mbti,
    String smokingStatus,
    String drinkingStatus,
    String religion,
    Set<String> hobbies,
    List<InterviewInfo> interviews
) {
}
