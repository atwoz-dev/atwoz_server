package deepple.deepple.mission.command.domain.mission;

import deepple.deepple.mission.command.domain.mission.exception.InvalidMissionEnumValueException;
import lombok.Getter;

@Getter
public enum ActionType {

    // TODO: 기획에 따른 활동 타입별 지정 필요.
    LIKE("좋아요"),
    INTERVIEW("인터뷰"),
    FIRST_DATE_EXAM("첫 연애모의고사 응시");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public static ActionType from(String actionType) {
        try {
            return ActionType.valueOf(actionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMissionEnumValueException(actionType);
        }
    }
}
