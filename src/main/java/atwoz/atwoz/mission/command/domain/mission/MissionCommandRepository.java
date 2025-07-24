package atwoz.atwoz.mission.command.domain.mission;

import java.util.List;

public interface MissionCommandRepository {
    List<Mission> findByActionTypeAndTargetGender(ActionType actionType, TargetGender targetGender);
}
