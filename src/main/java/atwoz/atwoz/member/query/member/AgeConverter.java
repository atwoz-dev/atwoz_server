package atwoz.atwoz.member.query.member;

import java.util.Calendar;

public class AgeConverter {
    public static Integer toAge(Integer yearOfBirth) {

        int current = Calendar.getInstance().get(Calendar.YEAR);
        int age = current - yearOfBirth + 1;
        System.out.println("current : " + current);
        System.out.println("age : " + age);
        System.out.println("year : " + (current - age - 1));
        return yearOfBirth == null ? null : Calendar.getInstance().get(Calendar.YEAR) - yearOfBirth + 1;
    }

    public static Integer toYearOfBirth(Integer age) {
        return age == null ? null : Calendar.getInstance().get(Calendar.YEAR) - age - 1;
    }
}
