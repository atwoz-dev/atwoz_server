package atwoz.atwoz.mission.command.infra.mission;

import atwoz.atwoz.mission.command.domain.mission.ActionType;
import atwoz.atwoz.mission.command.domain.mission.Mission;
import atwoz.atwoz.mission.command.domain.mission.TargetGender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionJpaRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByActionTypeAndTargetGender(ActionType actionType, TargetGender targetGender);
}
