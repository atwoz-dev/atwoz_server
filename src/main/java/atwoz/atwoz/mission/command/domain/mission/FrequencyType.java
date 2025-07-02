package atwoz.atwoz.mission.command.domain.mission;

import lombok.Getter;

@Getter
public enum FrequencyType {

    DAILY("일일"),
    CHALLENGE("챌린지");

    private final String description;

    FrequencyType(String description) {
        this.description = description;
    }
}
