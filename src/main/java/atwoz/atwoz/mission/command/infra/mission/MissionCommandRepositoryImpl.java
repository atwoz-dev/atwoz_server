package atwoz.atwoz.mission.command.infra.mission;

import atwoz.atwoz.mission.command.domain.mission.ActionType;
import atwoz.atwoz.mission.command.domain.mission.Mission;
import atwoz.atwoz.mission.command.domain.mission.MissionCommandRepository;
import atwoz.atwoz.mission.command.domain.mission.TargetGender;
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
