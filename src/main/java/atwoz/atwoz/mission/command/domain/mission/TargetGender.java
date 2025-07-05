package atwoz.atwoz.mission.command.domain.mission;

import atwoz.atwoz.mission.command.domain.mission.exception.InvalidMissionEnumValueException;
import lombok.Getter;

@Getter
public enum TargetGender {
    MALE("남성"),
    FEMALE("여성"),
    ALL("모두");

    private final String description;

    TargetGender(String description) {
        this.description = description;
    }

    public static TargetGender from(String targetGender) {
        try {
            return TargetGender.valueOf(targetGender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMissionEnumValueException(targetGender);
        }
    }
}
