package atwoz.atwoz.member.query.member;

import java.util.Calendar;

public class AgeConverter {
    public static Integer toAge(Integer yearOfBirth) {
        return yearOfBirth == null ? null : Calendar.getInstance().get(Calendar.YEAR) - yearOfBirth + 1;
    }

    public static Integer toYearOfBirth(Integer age) {
        return age == null ? null : Calendar.getInstance().get(Calendar.YEAR) - age - 1;
    }
}
