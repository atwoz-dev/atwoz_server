package atwoz.atwoz.mission.command.domain.mission;

import atwoz.atwoz.mission.command.domain.mission.exception.InvalidMissionEnumValueException;
import lombok.Getter;

@Getter
public enum FrequencyType {

    DAILY("일일"),
    CHALLENGE("챌린지");

    private final String description;

    FrequencyType(String description) {
        this.description = description;
    }

    public static FrequencyType from(String frequencyType) {
        try {
            return FrequencyType.valueOf(frequencyType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMissionEnumValueException(frequencyType);
        }
    }
}
