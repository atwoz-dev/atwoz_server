package atwoz.atwoz.member.command.domain.member.vo;

import atwoz.atwoz.member.command.domain.member.City;
import atwoz.atwoz.member.command.domain.member.District;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Region {
    private final City city;
    private final District district;

    public static Region of(District district) {
        return new Region(district);
    }

    private Region(@NonNull District district) {
        this.city = district.getCity();
        this.district = district;
    }
}
