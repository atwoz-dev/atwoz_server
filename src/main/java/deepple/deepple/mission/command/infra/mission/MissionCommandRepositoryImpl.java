package deepple.deepple.mission.command.infra.mission;

import deepple.deepple.mission.command.domain.mission.ActionType;
import deepple.deepple.mission.command.domain.mission.Mission;
import deepple.deepple.mission.command.domain.mission.MissionCommandRepository;
import deepple.deepple.mission.command.domain.mission.TargetGender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MissionCommandRepositoryImpl implements MissionCommandRepository {
    private final MissionJpaRepository missionJpaRepository;

    @Override
    public List<Mission> findByActionTypeAndTargetGender(ActionType actionType, TargetGender targetGender) {
        return missionJpaRepository.findByActionTypeAndTargetGender(actionType, targetGender);
    }

}
